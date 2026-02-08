package com.venom.data.repo

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.SetOptions
import com.venom.analytics.CrashlyticsManager
import com.venom.data.local.dao.UserWordProgressDao
import com.venom.data.local.dao.WordMasterDao
import com.venom.data.local.entity.UserWordProgressEntity
import com.venom.data.mapper.WordMasterEntityMapper
import com.venom.domain.model.DifficultyState
import com.venom.domain.model.KnownState
import com.venom.domain.model.SessionStatsData
import com.venom.domain.model.UserWordProgress
import com.venom.domain.srs.SrsEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserWordProgressRepository @Inject constructor(
    private val progressDao: UserWordProgressDao,
    private val wordMasterDao: WordMasterDao,
    private val srsEngine: SrsEngine,
    private val activityRepository: UserActivityRepository,
    private val crashlytics: CrashlyticsManager
) {

    suspend fun getOrCreateProgress(userId: String, wordId: Int): UserWordProgress =
        withContext(Dispatchers.IO) {
            progressDao.getProgress(userId, wordId)?.toDomain()
                ?: UserWordProgress(userId, wordId).also {
                    progressDao.upsert(it.toEntity())
                }
        }

    suspend fun getWordsDueForReview(
        userId: String,
        limit: Int = 10
    ): List<com.venom.domain.model.WordMaster> =
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val dueProgress = progressDao.getDueWords(userId, now, limit)

            dueProgress.mapNotNull { progress ->
                wordMasterDao.getWordById(progress.wordId)?.let { entity ->
                    WordMasterEntityMapper.toDomain(entity)
                }
            }
        }

    suspend fun recordRecallSuccess(userId: String, wordId: Int) = withContext(Dispatchers.IO) {
        val word = wordMasterDao.getWordById(wordId) ?: return@withContext

        val before = getOrCreateProgress(userId, wordId)

        updateProgress(userId, wordId) { progress ->
            srsEngine.onRecallSuccess(progress, word.rank ?: 0, word.frequency ?: 1)
        }

        val after = getOrCreateProgress(userId, wordId)
        if (after.knownState == KnownState.MASTERED && before.knownState != KnownState.MASTERED) {
            activityRepository.recordWordMastered(userId)
        }

        activityRepository.recordRecallSuccess(userId)
    }

    suspend fun recordRecallFail(userId: String, wordId: Int) = withContext(Dispatchers.IO) {
        updateProgress(userId, wordId) { progress ->
            srsEngine.onRecallFail(progress)
        }
        activityRepository.recordRecallFail(userId)
    }

    suspend fun recordProductionSuccess(userId: String, wordId: Int) = withContext(Dispatchers.IO) {
        val word = wordMasterDao.getWordById(wordId) ?: return@withContext

        val before = getOrCreateProgress(userId, wordId)

        updateProgress(userId, wordId) { progress ->
            srsEngine.onProductionSuccess(progress, word.rank ?: 0, word.frequency ?: 1)
        }

        val after = getOrCreateProgress(userId, wordId)
        if (after.knownState == KnownState.MASTERED && before.knownState != KnownState.MASTERED) {
            activityRepository.recordWordMastered(userId)
        }

        activityRepository.recordPracticeSuccess(userId)
    }

    suspend fun recordView(userId: String, wordId: Int) = withContext(Dispatchers.IO) {
        ensureProgressExists(userId, wordId)
        progressDao.incrementViewCount(userId, wordId, System.currentTimeMillis())
        activityRepository.recordWordView(userId)
    }

    suspend fun recordSwipeRight(userId: String, wordId: Int) = withContext(Dispatchers.IO) {
        updateProgress(userId, wordId) { it.copy(swipeRightCount = it.swipeRightCount + 1) }
        activityRepository.recordSwipeRight(userId)
    }

    suspend fun recordSwipeLeft(userId: String, wordId: Int) = withContext(Dispatchers.IO) {
        updateProgress(userId, wordId) { it.copy(swipeLeftCount = it.swipeLeftCount + 1) }
        activityRepository.recordSwipeLeft(userId)
    }

    suspend fun getSessionStats(userId: String): SessionStatsData = withContext(Dispatchers.IO) {
        val allProgress = progressDao.getAllProgressSnapshot(userId)
        val dashboard = activityRepository.getDashboardData(userId)

        SessionStatsData(
            totalWordsLearned = allProgress.size,
            masteredCount = allProgress.count { it.knownState == KnownState.MASTERED.name },
            learningCount = allProgress.count {
                it.knownState == KnownState.LEARNING.name || it.knownState == KnownState.KNOWN.name
            },
            needsReviewCount = allProgress.count {
                it.nextReview != null && it.nextReview <= System.currentTimeMillis()
            },
            currentStreak = dashboard.currentStreak,
            todayWordsViewed = dashboard.dailyGoalProgress,
            todayXpEarned = dashboard.todayXp
        )
    }

    suspend fun migrateLocalData(fromUserId: String, toUserId: String) =
        withContext(Dispatchers.IO) {
            try {
                val count = progressDao.getProgressCount(fromUserId)
                if (count > 0) {
                    progressDao.migrateUserId(fromUserId, toUserId)
                    Log.d(
                        "UserProgress",
                        "Migrated $count word progress records: $fromUserId → $toUserId"
                    )
                }
            } catch (e: Exception) {
                Log.e("UserProgress", "Migration failed: ${e.message}", e)
            }
        }

    // Firebase Sync
    private val firestore by lazy {
        try {
            val db = FirebaseFirestore.getInstance()
            val cacheSettings = PersistentCacheSettings.newBuilder()
                .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()
            val settings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(cacheSettings)
                .build()
            db.firestoreSettings = settings
            Log.d(TAG, "Firestore initialized successfully")
            db
        } catch (e: Exception) {
            Log.e(TAG, "Firestore initialization failed: ${e.message}", e)
            crashlytics.logNonFatalException(e, "Firestore init failed in UserWordProgressRepo")
            null
        }
    }

    suspend fun syncFromCloud(userId: String) = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            val snapshot = db.collection("users").document(userId)
                .collection("word_progress").get().await()

            if (snapshot.isEmpty) return@withContext

            val entitiesToSync = snapshot.documents.mapNotNull { doc ->
                try {
                    val data = doc.data ?: return@mapNotNull null
                    val wordId = doc.id.toIntOrNull() ?: return@mapNotNull null

                    UserWordProgressEntity(
                        userId = userId,
                        wordId = wordId,
                        viewCount = (data["viewCount"] as? Long)?.toInt() ?: 1,
                        lastViewed = (data["lastViewed"] as? Long) ?: System.currentTimeMillis(),
                        swipeRightCount = (data["swipeRightCount"] as? Long)?.toInt() ?: 0,
                        swipeLeftCount = (data["swipeLeftCount"] as? Long)?.toInt() ?: 0,
                        bookmarked = (data["bookmarked"] as? Boolean) ?: false,
                        practiceCompleted = (data["practiceCompleted"] as? Long)?.toInt() ?: 0,
                        productionSuccess = (data["productionSuccess"] as? Long)?.toInt() ?: 0,
                        recallSuccess = (data["recallSuccess"] as? Long)?.toInt() ?: 0,
                        recallFail = (data["recallFail"] as? Long)?.toInt() ?: 0,
                        knownState = (data["knownState"] as? String) ?: "NEW",
                        difficultyState = (data["difficultyState"] as? String) ?: "MEDIUM",
                        stability = (data["stability"] as? Double)?.toFloat() ?: 0f,
                        lapsesCount = (data["lapsesCount"] as? Long)?.toInt() ?: 0,
                        lastReview = (data["lastReview"] as? Long),
                        nextReview = (data["nextReview"] as? Long)
                    )
                } catch (_: Exception) {
                    null
                }
            }

            if (entitiesToSync.isNotEmpty()) {
                progressDao.upsertAll(entitiesToSync)
                Log.d(
                    "UserProgress",
                    "Successfully restored ${entitiesToSync.size} words from cloud."
                )
            }
        } catch (e: Exception) {
            Log.e("UserProgress", "Cloud restoration failed: ${e.message}", e)
        }
    }

    private suspend fun syncToCloud(progress: UserWordProgress) {
        val db = firestore
        if (db == null) {
            Log.w(TAG, "Firestore not initialized, skipping word progress sync")
            crashlytics.logBreadcrumb("Word progress sync skipped: Firestore null")
            return
        }

        val data = mapOf(
            "viewCount" to progress.viewCount,
            "lastViewed" to progress.lastViewed,
            "swipeRightCount" to progress.swipeRightCount,
            "swipeLeftCount" to progress.swipeLeftCount,
            "bookmarked" to progress.bookmarked,
            "practiceCompleted" to progress.practiceCompleted,
            "productionSuccess" to progress.productionSuccess,
            "recallSuccess" to progress.recallSuccess,
            "recallFail" to progress.recallFail,
            "knownState" to progress.knownState.name,
            "difficultyState" to progress.difficultyState.name,
            "stability" to progress.stability,
            "lapsesCount" to progress.lapsesCount,
            "lastReview" to progress.lastReview,
            "nextReview" to progress.nextReview
        )

        try {
            db.collection("users").document(progress.userId)
                .collection("word_progress").document(progress.wordId.toString())
                .set(data, SetOptions.merge())
                .await()
            Log.d(TAG, "Word progress synced for word: ${progress.wordId}")
        } catch (e: Exception) {
            Log.e(TAG, "Word progress sync failed: ${e.message}")
            crashlytics.logNonFatalException(e, "Word progress sync failed for word: ${progress.wordId}")
            throw e
        }
    }

    private suspend fun updateProgress(
        userId: String,
        wordId: Int,
        update: (UserWordProgress) -> UserWordProgress
    ) {
        val current = getOrCreateProgress(userId, wordId)
        val updated = update(current)
        progressDao.upsert(updated.toEntity())
        syncToCloud(updated)
    }

    suspend fun toggleBookmark(userId: String, wordId: Int, bookmarked: Boolean) =
        withContext(Dispatchers.IO) {
            ensureProgressExists(userId, wordId)
            progressDao.setBookmarked(userId, wordId, bookmarked)
            getProgressInternal(userId, wordId)?.let { entity ->
                syncToCloud(entity.toDomain())
            }
        }

    private suspend fun getProgressInternal(userId: String, wordId: Int) =
        progressDao.getProgress(userId, wordId)

    fun getBookmarkedWords(userId: String): Flow<List<UserWordProgress>> =
        progressDao.getBookmarkedWords(userId).map { list -> list.map { it.toDomain() } }

    private suspend fun ensureProgressExists(userId: String, wordId: Int) {
        if (progressDao.getProgress(userId, wordId) == null) {
            progressDao.upsert(UserWordProgress(userId, wordId).toEntity())
        }
    }

    // Mappers
    private fun UserWordProgressEntity.toDomain() = UserWordProgress(
        userId = userId,
        wordId = wordId,
        viewCount = viewCount,
        lastViewed = lastViewed,
        swipeRightCount = swipeRightCount,
        swipeLeftCount = swipeLeftCount,
        bookmarked = bookmarked,
        practiceCompleted = practiceCompleted,
        productionSuccess = productionSuccess,
        recallSuccess = recallSuccess,
        recallFail = recallFail,
        knownState = KnownState.valueOf(knownState),
        difficultyState = DifficultyState.valueOf(difficultyState),
        stability = stability,
        lapsesCount = lapsesCount,
        lastReview = lastReview,
        nextReview = nextReview
    )

    private fun UserWordProgress.toEntity() = UserWordProgressEntity(
        userId = userId,
        wordId = wordId,
        viewCount = viewCount,
        lastViewed = lastViewed,
        swipeRightCount = swipeRightCount,
        swipeLeftCount = swipeLeftCount,
        bookmarked = bookmarked,
        practiceCompleted = practiceCompleted,
        productionSuccess = productionSuccess,
        recallSuccess = recallSuccess,
        recallFail = recallFail,
        knownState = knownState.name,
        difficultyState = difficultyState.name,
        stability = stability,
        lapsesCount = lapsesCount,
        lastReview = lastReview,
        nextReview = nextReview
    )

    companion object {
        private const val TAG = "UserWordProgressRepo"
    }
}
