package com.venom.data.repo

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.SetOptions
import com.venom.data.local.dao.UserActivityDao
import com.venom.data.local.dao.UserProfileDao
import com.venom.data.local.entity.UserActivityEntity
import com.venom.data.local.entity.UserProfileEntity
import com.venom.domain.model.DashboardData
import com.venom.domain.model.StreakEngine
import com.venom.domain.model.UserLevel
import com.venom.domain.model.XpResult
import com.venom.domain.model.XpRewards
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserActivityRepository @Inject constructor(
    private val activityDao: UserActivityDao,
    private val profileDao: UserProfileDao
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val firestore: FirebaseFirestore? by lazy {
        try {
            val db = FirebaseFirestore.getInstance()
            db.firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(
                    PersistentCacheSettings.newBuilder()
                        .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                        .build()
                ).build()
            db
        } catch (_: Exception) {
            null
        }
    }

    private fun today(): String = dateFormat.format(Date())

    private fun yesterday(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return dateFormat.format(cal.time)
    }

    private fun previousDay(dateKey: String): String {
        return try {
            val date = dateFormat.parse(dateKey) ?: return dateKey
            val cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.DAY_OF_YEAR, -1)
            dateFormat.format(cal.time)
        } catch (_: Exception) {
            dateKey
        }
    }

    suspend fun getOrCreateProfile(userId: String): UserProfileEntity =
        withContext(Dispatchers.IO) {
            profileDao.getProfile(userId) ?: UserProfileEntity(
                userId = userId,
                joinedAt = System.currentTimeMillis()
            ).also { profileDao.upsert(it) }
        }

    suspend fun rebuildProfile(userId: String) = withContext(Dispatchers.IO) {
        val allActivities = activityDao.getAllActivities(userId)
        val activeDays = activityDao.getActiveDays(userId)
        val totalActiveDays = activityDao.getTotalActiveDays(userId)
        val totalXp = activityDao.getTotalXp(userId) ?: 0

        val streak = StreakEngine.calculateStreak(
            activeDays, today(), yesterday(), ::previousDay
        )

        val existing = getOrCreateProfile(userId)

        val wordsViewed = allActivities.sumOf { it.wordsViewed }
        val mastered = allActivities.sumOf { it.masteredCount }
        val sessions = allActivities.sumOf { it.sessionCount }
        val timeMs = allActivities.sumOf { it.totalTimeMs }

        val rebuilt = existing.copy(
            totalXp = totalXp,
            level = UserLevel.levelNumber(totalXp),
            currentStreak = streak,
            bestStreak = maxOf(existing.bestStreak, streak),
            lastActiveDate = activeDays.firstOrNull() ?: "",
            totalWordsViewed = wordsViewed,
            totalWordsMastered = mastered,
            totalSessionCount = sessions,
            totalTimeMs = timeMs,
            totalDaysActive = totalActiveDays,
            updatedAt = System.currentTimeMillis()
        )

        profileDao.upsert(rebuilt)
        syncProfileToCloud(userId, rebuilt)
    }

    private suspend fun ensureTodayExists(userId: String) {
        val todayKey = today()
        if (activityDao.getActivityForDate(userId, todayKey) == null) {
            val profile = getOrCreateProfile(userId)
            activityDao.upsert(
                UserActivityEntity(
                    userId = userId,
                    dateKey = todayKey,
                    dailyGoalTarget = profile.dailyGoalXp
                )
            )
            updateStreakOnNewDay(userId)
        }
    }

    private suspend fun awardXp(
        userId: String,
        baseXp: Int
    ): XpResult = withContext(Dispatchers.IO) {
        val todayKey = today()
        val profile = getOrCreateProfile(userId)

        val multiplier = XpRewards.streakMultiplier(profile.currentStreak)
        val totalXp = (baseXp * multiplier).toInt()

        activityDao.addXp(userId, todayKey, totalXp, System.currentTimeMillis())

        val newTotalXp = profile.totalXp + totalXp
        val oldLevel = UserLevel.fromXp(profile.totalXp)
        val newLevel = UserLevel.fromXp(newTotalXp)
        val leveledUp = newLevel.ordinal > oldLevel.ordinal

        profileDao.addXpAndUpdateLevel(
            userId, totalXp, UserLevel.levelNumber(newTotalXp), System.currentTimeMillis()
        )

        val todayActivity = activityDao.getActivityForDate(userId, todayKey)
        val dailyGoalJustMet = todayActivity != null &&
                !todayActivity.dailyGoalMet &&
                todayActivity.totalXpEarned >= todayActivity.dailyGoalTarget

        if (dailyGoalJustMet) {
            activityDao.markDailyGoalMet(
                userId, todayKey, XpRewards.DAILY_GOAL_BONUS, System.currentTimeMillis()
            )
        }

        XpResult(
            baseXp = baseXp,
            streakMultiplier = multiplier,
            totalXp = totalXp,
            newTotalXp = newTotalXp,
            newLevel = newLevel,
            leveledUp = leveledUp,
            dailyGoalJustMet = dailyGoalJustMet
        )
    }

    private suspend fun updateProfileAggregates(userId: String) {
        val allActivities = activityDao.getAllActivities(userId)
        val totalActiveDays = activityDao.getTotalActiveDays(userId)
        profileDao.updateAggregates(
            userId = userId,
            wordsViewed = allActivities.sumOf { it.wordsViewed },
            mastered = allActivities.sumOf { it.masteredCount },
            sessions = allActivities.sumOf { it.sessionCount },
            timeMs = allActivities.sumOf { it.totalTimeMs },
            daysActive = totalActiveDays,
            now = System.currentTimeMillis()
        )
    }

    suspend fun recordWordView(userId: String): XpResult = withContext(Dispatchers.IO) {
        ensureTodayExists(userId)
        activityDao.incrementWordsViewed(userId, today(), System.currentTimeMillis())
        val result = awardXp(userId, XpRewards.WORD_VIEW)
        updateProfileAggregates(userId)
        syncDayToCloud(userId, today())
        result
    }

    suspend fun recordSwipeRight(userId: String): XpResult = withContext(Dispatchers.IO) {
        ensureTodayExists(userId)
        activityDao.incrementSwipeRight(userId, today(), System.currentTimeMillis())
        val result = awardXp(userId, XpRewards.SWIPE_REMEMBER)
        updateProfileAggregates(userId)
        syncDayToCloud(userId, today())
        result
    }

    suspend fun recordSwipeLeft(userId: String): XpResult = withContext(Dispatchers.IO) {
        ensureTodayExists(userId)
        activityDao.incrementSwipeLeft(userId, today(), System.currentTimeMillis())
        val result = awardXp(userId, XpRewards.SWIPE_FORGOT)
        updateProfileAggregates(userId)
        syncDayToCloud(userId, today())
        result
    }

    suspend fun recordRecallSuccess(userId: String): XpResult = withContext(Dispatchers.IO) {
        ensureTodayExists(userId)
        activityDao.incrementRecallSuccess(userId, today(), 0, System.currentTimeMillis())
        val result = awardXp(userId, XpRewards.RECALL_SUCCESS)
        updateProfileAggregates(userId)
        syncDayToCloud(userId, today())
        result
    }

    suspend fun recordRecallFail(userId: String) = withContext(Dispatchers.IO) {
        ensureTodayExists(userId)
        activityDao.incrementRecallFail(userId, today(), System.currentTimeMillis())
        updateProfileAggregates(userId)
        syncDayToCloud(userId, today())
    }

    suspend fun recordPracticeSuccess(userId: String): XpResult = withContext(Dispatchers.IO) {
        ensureTodayExists(userId)
        activityDao.incrementPracticeSuccess(userId, today(), 0, System.currentTimeMillis())
        val result = awardXp(userId, XpRewards.PRACTICE_SUCCESS)
        updateProfileAggregates(userId)
        syncDayToCloud(userId, today())
        result
    }

    suspend fun recordWordMastered(userId: String): XpResult = withContext(Dispatchers.IO) {
        ensureTodayExists(userId)
        activityDao.incrementMastered(userId, today(), 0, System.currentTimeMillis())
        profileDao.incrementMastered(userId, System.currentTimeMillis())
        val result = awardXp(userId, XpRewards.WORD_MASTERED)
        updateProfileAggregates(userId)
        syncDayToCloud(userId, today())
        result
    }

    suspend fun recordPerfectSession(userId: String) = withContext(Dispatchers.IO) {
        ensureTodayExists(userId)
        activityDao.incrementPerfectSessions(userId, today(), System.currentTimeMillis())
        syncDayToCloud(userId, today())
    }

    suspend fun recordSessionComplete(userId: String, durationMs: Long) =
        withContext(Dispatchers.IO) {
            ensureTodayExists(userId)
            val now = System.currentTimeMillis()
            activityDao.incrementSessionCount(userId, today(), now)
            activityDao.addSessionTime(userId, today(), durationMs, now)
            updateProfileAggregates(userId)
            syncDayToCloud(userId, today())
            syncProfileToCloud(userId, getOrCreateProfile(userId))
        }

    suspend fun addSessionTime(userId: String, durationMs: Long) =
        withContext(Dispatchers.IO) {
            ensureTodayExists(userId)
            activityDao.addSessionTime(userId, today(), durationMs, System.currentTimeMillis())
            updateProfileAggregates(userId)
            syncDayToCloud(userId, today())
        }

    private suspend fun updateStreakOnNewDay(userId: String) {
        val activeDays = activityDao.getActiveDays(userId)
        val todayKey = today()
        val yesterdayKey = yesterday()

        val profile = getOrCreateProfile(userId)

        val needsFreeze = StreakEngine.shouldConsumeFreeze(activeDays, todayKey, yesterdayKey)
        if (needsFreeze && profile.streakFreezes > 0) {
            profileDao.consumeStreakFreeze(userId, System.currentTimeMillis())
        }

        val updatedDays = if (activeDays.firstOrNull() != todayKey) {
            listOf(todayKey) + activeDays
        } else activeDays

        val streak = StreakEngine.calculateStreak(
            updatedDays, todayKey, yesterdayKey, ::previousDay
        )

        profileDao.updateStreak(userId, streak, todayKey, System.currentTimeMillis())

        StreakEngine.checkMilestone(streak)?.let { milestone ->
            activityDao.addXp(
                userId, todayKey, milestone.bonusXp, System.currentTimeMillis()
            )
            profileDao.addXpAndUpdateLevel(
                userId, milestone.bonusXp,
                UserLevel.levelNumber(profile.totalXp + milestone.bonusXp),
                System.currentTimeMillis()
            )
        }

        if (profile.lastActiveDate != todayKey) {
            activityDao.addXp(
                userId, todayKey, XpRewards.FIRST_SESSION_BONUS, System.currentTimeMillis()
            )
        }

        syncProfileToCloud(userId, getOrCreateProfile(userId))
    }

    internal suspend fun getCurrentStreak(userId: String): Int = withContext(Dispatchers.IO) {
        try {
            val activeDays = activityDao.getActiveDays(userId)
            StreakEngine.calculateStreak(activeDays, today(), yesterday(), ::previousDay)
        } catch (e: Exception) {
            Log.e("Activity", "Streak calc error: ${e.message}")
            0
        }
    }

    suspend fun getTodayActivity(userId: String): UserActivityEntity? =
        withContext(Dispatchers.IO) {
            activityDao.getActivityForDate(userId, today())
        }

    suspend fun getRecentActivity(userId: String, days: Int = 7): List<UserActivityEntity> =
        withContext(Dispatchers.IO) {
            activityDao.getRecentActivities(userId, days)
        }

    suspend fun getDashboardData(userId: String): DashboardData =
        withContext(Dispatchers.IO) {
            val profile = getOrCreateProfile(userId)
            val today = getTodayActivity(userId)
            val level = UserLevel.fromXp(profile.totalXp)

            DashboardData(
                totalXp = profile.totalXp,
                todayXp = today?.totalXpEarned ?: 0,
                level = level,
                levelProgress = UserLevel.levelProgress(profile.totalXp),
                xpToNextLevel = UserLevel.xpToNextLevel(profile.totalXp),
                currentStreak = profile.currentStreak,
                bestStreak = profile.bestStreak,
                totalWordsViewed = profile.totalWordsViewed,
                totalWordsMastered = profile.totalWordsMastered,
                totalSessionCount = profile.totalSessionCount,
                totalTimeMs = profile.totalTimeMs,
                totalDaysActive = profile.totalDaysActive,
                dailyGoalTarget = profile.dailyGoalXp,
                dailyGoalProgress = today?.totalXpEarned ?: 0,
                dailyGoalMet = today?.dailyGoalMet ?: false,
                streakFreezes = profile.streakFreezes
            )
        }

    suspend fun updateDailyGoal(userId: String, goalXp: Int) =
        withContext(Dispatchers.IO) {
            profileDao.updateDailyGoal(userId, goalXp, System.currentTimeMillis())
        }

    suspend fun migrateLocalData(fromUserId: String, toUserId: String) =
        withContext(Dispatchers.IO) {
            try {
                activityDao.migrateUserId(fromUserId, toUserId)
                profileDao.migrateUserId(fromUserId, toUserId)
            } catch (e: Exception) {
                Log.e("Activity", "Migration failed: ${e.message}", e)
            }
        }

    private suspend fun syncDayToCloud(userId: String, dateKey: String) {
        try {
            val activity = activityDao.getActivityForDate(userId, dateKey) ?: return
            syncActivityDocToCloud(userId, activity)
        } catch (e: Exception) {
            Log.e("Activity", "Cloud sync failed for $dateKey: ${e.message}")
        }
    }

    private fun syncActivityDocToCloud(userId: String, activity: UserActivityEntity) {
        val db = firestore ?: return
        val data = mapOf(
            "wordsViewed" to activity.wordsViewed,
            "wordsSwipedRight" to activity.wordsSwipedRight,
            "wordsSwipedLeft" to activity.wordsSwipedLeft,
            "wordsPracticed" to activity.wordsPracticed,
            "sessionCount" to activity.sessionCount,
            "totalTimeMs" to activity.totalTimeMs,
            "longestSessionMs" to activity.longestSessionMs,
            "recallSuccess" to activity.recallSuccess,
            "recallFail" to activity.recallFail,
            "productionSuccess" to activity.productionSuccess,
            "perfectSessions" to activity.perfectSessions,
            "masteredCount" to activity.masteredCount,
            "totalXpEarned" to activity.totalXpEarned,
            "dailyGoalTarget" to activity.dailyGoalTarget,
            "dailyGoalMet" to activity.dailyGoalMet,
            "updatedAt" to activity.updatedAt
        )

        db.collection("users").document(userId)
            .collection("activity").document(activity.dateKey)
            .set(data, SetOptions.merge())
            .addOnFailureListener { Log.e("Activity", "Cloud sync failed: ${it.message}") }
    }

    private fun syncProfileToCloud(userId: String, profile: UserProfileEntity) {
        val db = firestore ?: return
        val data = mapOf(
            "totalXp" to profile.totalXp,
            "level" to profile.level,
            "currentStreak" to profile.currentStreak,
            "bestStreak" to profile.bestStreak,
            "lastActiveDate" to profile.lastActiveDate,
            "streakFreezes" to profile.streakFreezes,
            "totalWordsViewed" to profile.totalWordsViewed,
            "totalWordsMastered" to profile.totalWordsMastered,
            "totalSessionCount" to profile.totalSessionCount,
            "totalTimeMs" to profile.totalTimeMs,
            "totalDaysActive" to profile.totalDaysActive,
            "dailyGoalXp" to profile.dailyGoalXp,
            "joinedAt" to profile.joinedAt,
            "updatedAt" to profile.updatedAt
        )

        db.collection("users").document(userId)
            .collection("profile").document("main")
            .set(data, SetOptions.merge())
            .addOnFailureListener { Log.e("Activity", "Profile sync failed: ${it.message}") }
    }

    suspend fun syncFromCloud(userId: String) = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext

        try {
            val activitySnap = db.collection("users").document(userId)
                .collection("activity").get().await()

            for (doc in activitySnap.documents) {
                try {
                    val data = doc.data ?: continue
                    val cloud = UserActivityEntity(
                        userId = userId,
                        dateKey = doc.id,
                        wordsViewed = (data["wordsViewed"] as? Long)?.toInt() ?: 0,
                        wordsSwipedRight = (data["wordsSwipedRight"] as? Long)?.toInt() ?: 0,
                        wordsSwipedLeft = (data["wordsSwipedLeft"] as? Long)?.toInt() ?: 0,
                        wordsPracticed = (data["wordsPracticed"] as? Long)?.toInt() ?: 0,
                        sessionCount = (data["sessionCount"] as? Long)?.toInt() ?: 0,
                        totalTimeMs = (data["totalTimeMs"] as? Long) ?: 0,
                        longestSessionMs = (data["longestSessionMs"] as? Long) ?: 0,
                        recallSuccess = (data["recallSuccess"] as? Long)?.toInt() ?: 0,
                        recallFail = (data["recallFail"] as? Long)?.toInt() ?: 0,
                        productionSuccess = (data["productionSuccess"] as? Long)?.toInt() ?: 0,
                        perfectSessions = (data["perfectSessions"] as? Long)?.toInt() ?: 0,
                        masteredCount = (data["masteredCount"] as? Long)?.toInt() ?: 0,
                        totalXpEarned = (data["totalXpEarned"] as? Long)?.toInt() ?: 0,
                        dailyGoalTarget = (data["dailyGoalTarget"] as? Long)?.toInt() ?: 50,
                        dailyGoalMet = (data["dailyGoalMet"] as? Boolean) ?: false,
                        updatedAt = (data["updatedAt"] as? Long) ?: System.currentTimeMillis()
                    )

                    val local = activityDao.getActivityForDate(userId, doc.id)
                    if (local == null) {
                        activityDao.upsert(cloud)
                    } else {
                        activityDao.upsert(
                            local.copy(
                                wordsViewed = maxOf(local.wordsViewed, cloud.wordsViewed),
                                wordsSwipedRight = maxOf(
                                    local.wordsSwipedRight,
                                    cloud.wordsSwipedRight
                                ),
                                wordsSwipedLeft = maxOf(
                                    local.wordsSwipedLeft,
                                    cloud.wordsSwipedLeft
                                ),
                                wordsPracticed = maxOf(local.wordsPracticed, cloud.wordsPracticed),
                                sessionCount = maxOf(local.sessionCount, cloud.sessionCount),
                                totalTimeMs = maxOf(local.totalTimeMs, cloud.totalTimeMs),
                                longestSessionMs = maxOf(
                                    local.longestSessionMs,
                                    cloud.longestSessionMs
                                ),
                                recallSuccess = maxOf(local.recallSuccess, cloud.recallSuccess),
                                recallFail = maxOf(local.recallFail, cloud.recallFail),
                                productionSuccess = maxOf(
                                    local.productionSuccess,
                                    cloud.productionSuccess
                                ),
                                perfectSessions = maxOf(
                                    local.perfectSessions,
                                    cloud.perfectSessions
                                ),
                                masteredCount = maxOf(local.masteredCount, cloud.masteredCount),
                                totalXpEarned = maxOf(local.totalXpEarned, cloud.totalXpEarned),
                                dailyGoalMet = local.dailyGoalMet || cloud.dailyGoalMet,
                                updatedAt = maxOf(local.updatedAt, cloud.updatedAt)
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.w("Activity", "Parse failed for ${doc.id}: ${e.message}")
                }
            }

            val profileDoc = db.collection("users").document(userId)
                .collection("profile").document("main").get().await()

            if (profileDoc.exists()) {
                val data = profileDoc.data
                if (data != null) {
                    val local = getOrCreateProfile(userId)
                    profileDao.upsert(
                        local.copy(
                            totalXp = maxOf(
                                local.totalXp,
                                (data["totalXp"] as? Long)?.toInt() ?: 0
                            ),
                            bestStreak = maxOf(
                                local.bestStreak,
                                (data["bestStreak"] as? Long)?.toInt() ?: 0
                            ),
                            streakFreezes = maxOf(
                                local.streakFreezes,
                                (data["streakFreezes"] as? Long)?.toInt() ?: 0
                            ),
                            dailyGoalXp = (data["dailyGoalXp"] as? Long)?.toInt()
                                ?: local.dailyGoalXp,
                            joinedAt = minOf(
                                local.joinedAt,
                                (data["joinedAt"] as? Long) ?: local.joinedAt
                            )
                        )
                    )
                }
            }

            rebuildProfile(userId)
        } catch (e: Exception) {
            Log.e("Activity", "Cloud sync failed: ${e.message}", e)
        }
    }

    suspend fun syncAllToCloud(userId: String) = withContext(Dispatchers.IO) {
        try {
            activityDao.getAllActivities(userId).forEach { activity ->
                syncActivityDocToCloud(userId, activity)
            }
            syncProfileToCloud(userId, getOrCreateProfile(userId))
        } catch (e: Exception) {
            Log.e("Activity", "Push to cloud failed: ${e.message}", e)
        }
    }
}
