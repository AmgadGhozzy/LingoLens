package com.venom.data.repo

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.venom.data.local.dao.UserIdentityDao
import com.venom.data.local.entity.UserIdentityEntity
import com.venom.domain.model.UserIdentity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserIdentityRepository @Inject constructor(
    private val dao: UserIdentityDao,
    private val context: Context
) {
    private val auth = FirebaseAuth.getInstance()
    // Observable auth state
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Listen to auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _authState.value = AuthState(
                isSignedIn = user != null && !user.isAnonymous,
                userName = user?.displayName,
                userEmail = user?.email,
                photoUrl = user?.photoUrl?.toString()
            )
        }
    }
    /**
     * Get or create user identity using Firebase Auth.
     */
    suspend fun getOrCreateIdentity(): UserIdentity = withContext(Dispatchers.IO) {
        val currentUser = auth.currentUser ?: auth.signInAnonymously().await().user
        val userId = currentUser?.uid ?: throw IllegalStateException("Firebase Auth failed")

        Log.d("UserIdentity", "Authenticated with Firebase UID: $userId (Anonymous: ${currentUser.isAnonymous})")

        val existing = dao.getIdentity()
        if (existing != null && existing.userId == userId) {
            dao.updateLastActive(System.currentTimeMillis())
            return@withContext existing.toDomain()
        }

        val deviceId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: UUID.randomUUID().toString()

        val identity = UserIdentity(
            userId = userId,
            deviceId = deviceId,
            isAnonymous = currentUser.isAnonymous,
            createdAt = currentUser.metadata?.creationTimestamp ?: System.currentTimeMillis(),
            lastActiveAt = System.currentTimeMillis()
        )

        dao.insertOrUpdate(identity.toEntity())
        identity
    }

    suspend fun getCurrentUserId(): String = getOrCreateIdentity().userId

    /**
     * Get current user's display name from Firebase Auth.
     * Returns null if user is anonymous or name is not set.
     */
    fun getCurrentUserName(): String? {
        val user = auth.currentUser ?: return null
        return if (user.isAnonymous) null else user.displayName
    }

    /**
     * Get current user's email from Firebase Auth.
     * Returns null if user is anonymous or email is not set.
     */
    fun getCurrentUserEmail(): String? {
        val user = auth.currentUser ?: return null
        return if (user.isAnonymous) null else user.email
    }

    /**
     * Check if current user is signed in (not anonymous).
     */
    fun isSignedIn(): Boolean {
        val user = auth.currentUser ?: return false
        return !user.isAnonymous
    }

    /**
     * Get current user's photo URL from Firebase Auth.
     */
    fun getCurrentUserPhotoUrl(): String? {
        val user = auth.currentUser ?: return null
        return if (user.isAnonymous) null else user.photoUrl?.toString()
    }

    /**
     * Signs in with Google and links to the current anonymous account if applicable.
     */
    suspend fun signInWithGoogle(idToken: String): UserIdentity = withContext(Dispatchers.IO) {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        val currentUser = auth.currentUser

        val result = if (currentUser != null && currentUser.isAnonymous) {
            currentUser.linkWithCredential(credential).await()
        } else {
            auth.signInWithCredential(credential).await()
        }

        val user = result.user ?: throw IllegalStateException("Firebase Google Auth failed")

        val identity = UserIdentity(
            userId = user.uid,
            deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "",
            isAnonymous = false,
            createdAt = user.metadata?.creationTimestamp ?: System.currentTimeMillis(),
            lastActiveAt = System.currentTimeMillis()
        )

        dao.insertOrUpdate(identity.toEntity())
        identity
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