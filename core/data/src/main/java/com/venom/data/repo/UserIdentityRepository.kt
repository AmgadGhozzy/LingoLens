package com.venom.data.repo

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
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

data class SignInResult(
    val identity: UserIdentity,
    val previousAnonymousUid: String? = null
)

@Singleton
class UserIdentityRepository @Inject constructor(
    private val dao: UserIdentityDao,
    private val context: Context
) {
    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            
            // Find the Google provider if linked
            val googleProvider = user?.providerData?.find { 
                it.providerId == GoogleAuthProvider.PROVIDER_ID 
            }
            val hasGoogle = googleProvider != null

            _authState.value = AuthState(
                isSignedIn = user != null && hasGoogle,
                userName = googleProvider?.displayName ?: user?.displayName,
                userEmail = googleProvider?.email ?: user?.email,
                photoUrl = googleProvider?.photoUrl?.toString() ?: user?.photoUrl?.toString()
            )
        }
    }

    suspend fun getOrCreateIdentity(): UserIdentity = withContext(Dispatchers.IO) {
        val currentUser = auth.currentUser ?: auth.signInAnonymously().await().user
        val userId = currentUser?.uid ?: throw IllegalStateException("Firebase Auth failed")

        Log.d("UserIdentity", "Auth UID: $userId (Anonymous: ${currentUser.isAnonymous})")

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

    private fun getGoogleProvider() = auth.currentUser?.providerData?.find { 
        it.providerId == GoogleAuthProvider.PROVIDER_ID 
    }

    fun isSignedIn(): Boolean = getGoogleProvider() != null

    /**
     * Signs in with Google and links to the current anonymous account if applicable.
     */
    suspend fun signInWithGoogle(idToken: String): SignInResult = withContext(Dispatchers.IO) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val currentUser = auth.currentUser

        // Remember the anonymous UID before we potentially lose it
        val anonymousUid = if (currentUser?.isAnonymous == true) currentUser.uid else null

        Log.d("UserIdentity", "Sign-in attempt. Current user: ${currentUser?.uid}, anonymous: ${currentUser?.isAnonymous}")

        val firebaseUser = when {
            // Case 1 & 2: Currently anonymous → try to link
            currentUser != null && currentUser.isAnonymous -> {
                try {
                    // Case 1: Link succeeds — anonymous account upgraded to Google
                    val result = currentUser.linkWithCredential(credential).await()
                    Log.d("UserIdentity", "Link succeeded. UID unchanged: ${result.user?.uid}")
                    result.user
                } catch (_: FirebaseAuthUserCollisionException) {
                    Log.w("UserIdentity",
                        "Link collision: Google already linked to another account. " +
                                "Falling back to signInWithCredential. Anonymous UID to migrate: $anonymousUid"
                    )

                    // Clean up the orphaned anonymous account
                    try {
                        currentUser.delete().await()
                        Log.d("UserIdentity", "Deleted orphaned anonymous account: $anonymousUid")
                    } catch (deleteError: Exception) {
                        Log.w("UserIdentity", "Could not delete anonymous account: ${deleteError.message}")
                    }

                    // Sign in directly — this returns the ORIGINAL Google-linked UID
                    val result = auth.signInWithCredential(credential).await()
                    Log.d("UserIdentity", "Direct sign-in succeeded. Restored UID: ${result.user?.uid}")
                    result.user
                }
            }

            // Case 3: No current user, or already a non-anonymous user
            else -> {
                val result = auth.signInWithCredential(credential).await()
                Log.d("UserIdentity", "Direct sign-in. UID: ${result.user?.uid}")
                result.user
            }
        } ?: throw IllegalStateException("Firebase Google Auth failed — no user returned")

        val resolvedUid = firebaseUser.uid

        // Determine if migration is needed:
        // Migration needed when we had an anonymous account AND the UID changed
        val needsMigration = anonymousUid != null && anonymousUid != resolvedUid

        Log.d("UserIdentity",
            "Sign-in complete. UID: $resolvedUid, " +
                    "migration needed: $needsMigration" +
                    if (needsMigration) " (from $anonymousUid)" else ""
        )

        // Update local identity
        val deviceId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: ""

        val identity = UserIdentity(
            userId = resolvedUid,
            deviceId = deviceId,
            isAnonymous = false,
            createdAt = firebaseUser.metadata?.creationTimestamp ?: System.currentTimeMillis(),
            lastActiveAt = System.currentTimeMillis()
        )

        // Clean up old identity record if UID changed
        if (needsMigration) {
            dao.deleteByUserId(anonymousUid)
        }
        dao.insertOrUpdate(identity.toEntity())

        SignInResult(
            identity = identity,
            previousAnonymousUid = if (needsMigration) anonymousUid else null
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