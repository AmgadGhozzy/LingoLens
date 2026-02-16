package com.venom.data.repo

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.venom.data.local.dao.UserIdentityDao
import com.venom.data.local.entity.UserIdentityEntity
import com.venom.domain.model.UserIdentity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class SignInResult(
    val identity: UserIdentity,
    val previousAnonymousUid: String? = null
)

@Singleton
class UserIdentityRepository @Inject constructor(
    private val dao: UserIdentityDao,
    private val context: Context,
    private val sessionManager: SupabaseSessionManager
) {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        refreshAuthState()
    }

    private fun refreshAuthState() {
        _authState.value = AuthState(
            isSignedIn = sessionManager.isSignedIn,
            userName = sessionManager.userName,
            userEmail = sessionManager.userEmail,
            photoUrl = sessionManager.userAvatarUrl
        )
    }

    suspend fun getOrCreateIdentity(): UserIdentity = withContext(Dispatchers.IO) {
        if (sessionManager.hasSession && sessionManager.userId != null) {
            val userId = sessionManager.userId!!
            val existing = dao.getIdentity()
            if (existing != null && existing.userId == userId) {
                dao.updateLastActive(System.currentTimeMillis())
                return@withContext existing.toDomain()
            }
            val identity = createLocalIdentity(userId, sessionManager.isAnonymous)
            dao.insertOrUpdate(identity.toEntity())
            return@withContext identity
        }

        val authResponse = sessionManager.signUpAnonymous()
            ?: throw IllegalStateException("Supabase anonymous sign-up failed")

        val userId = authResponse.user?.id
            ?: throw IllegalStateException("Supabase returned no user")

        Log.d("UserIdentity", "Anonymous Supabase user: $userId")

        val identity = createLocalIdentity(userId, isAnonymous = true)
        dao.insertOrUpdate(identity.toEntity())
        refreshAuthState()
        identity
    }

    suspend fun getCurrentUserId(): String = getOrCreateIdentity().userId

    fun isSignedIn(): Boolean = sessionManager.isSignedIn

    suspend fun signInWithGoogle(idToken: String): SignInResult = withContext(Dispatchers.IO) {
        val currentIdentity = dao.getIdentity()
        val anonymousUid = if (currentIdentity?.isAnonymous == true) currentIdentity.userId else null

        Log.d("UserIdentity", "Sign-in attempt. Current user: ${currentIdentity?.userId}, anonymous: ${currentIdentity?.isAnonymous}")

        val authResponse = sessionManager.signInWithGoogle(idToken)
            ?: throw IllegalStateException("Supabase Google sign-in failed")

        val supabaseUser = authResponse.user
            ?: throw IllegalStateException("Supabase returned no user")

        val resolvedUid = supabaseUser.id

        val needsMigration = anonymousUid != null && anonymousUid != resolvedUid

        Log.d(
            "UserIdentity",
            "Sign-in complete. UID: $resolvedUid, " +
                    "migration needed: $needsMigration" +
                    if (needsMigration) " (from $anonymousUid)" else ""
        )

        val deviceId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: ""

        val identity = UserIdentity(
            userId = resolvedUid,
            deviceId = deviceId,
            isAnonymous = false,
            createdAt = System.currentTimeMillis(),
            lastActiveAt = System.currentTimeMillis()
        )

        if (needsMigration && anonymousUid != null) {
            dao.deleteByUserId(anonymousUid)
        }
        dao.insertOrUpdate(identity.toEntity())
        refreshAuthState()

        SignInResult(
            identity = identity,
            previousAnonymousUid = if (needsMigration) anonymousUid else null
        )
    }

    private fun createLocalIdentity(userId: String, isAnonymous: Boolean): UserIdentity {
        val deviceId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: UUID.randomUUID().toString()

        return UserIdentity(
            userId = userId,
            deviceId = deviceId,
            isAnonymous = isAnonymous,
            createdAt = System.currentTimeMillis(),
            lastActiveAt = System.currentTimeMillis()
        )
    }

    private fun UserIdentityEntity.toDomain() = UserIdentity(
        userId = userId,
        deviceId = deviceId,
        isAnonymous = isAnonymous,
        createdAt = createdAt,
        lastActiveAt = lastActiveAt
    )

    private fun UserIdentity.toEntity() = UserIdentityEntity(
        userId = userId,
        deviceId = deviceId,
        isAnonymous = isAnonymous,
        createdAt = createdAt,
        lastActiveAt = lastActiveAt
    )

    data class AuthState(
        val isSignedIn: Boolean = false,
        val userName: String? = null,
        val userEmail: String? = null,
        val photoUrl: String? = null
    )
}