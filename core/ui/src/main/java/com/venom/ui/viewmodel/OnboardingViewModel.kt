package com.venom.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.repo.UserActivityRepository
import com.venom.data.repo.UserIdentityRepository
import com.venom.data.repo.UserWordProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val identityRepository: UserIdentityRepository,
    private val progressRepository: UserWordProgressRepository,
    private val activityRepository: UserActivityRepository
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)

    fun restoreUserProgress(onComplete: () -> Unit) {
        viewModelScope.launch {
            _isSyncing.update { true }
            try {
                val userId = identityRepository.getCurrentUserId()
                // Ensure profile and today's activity exist locally
                activityRepository.ensureTodayExists(userId)
            } catch (e: Exception) {
                Log.e("OnboardingVM", "Restore failed: ${e.message}", e)
            } finally {
                _isSyncing.update { false }
                onComplete()
            }
        }
    }

    fun onGoogleSignInResult(idToken: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _isSyncing.update { true }
            try {
                val result = identityRepository.signInWithGoogle(idToken)
                val userId = result.identity.userId

                // Migrate local data if UID changed
                result.previousAnonymousUid?.let { oldUid ->
                    Log.d("OnboardingVM", "Migrating data: $oldUid → $userId")
                    progressRepository.migrateLocalData(oldUid, userId)
                    activityRepository.migrateLocalData(oldUid, userId)
                }

                // Ensure profile and today's activity exist
                activityRepository.ensureTodayExists(userId)

            } catch (e: Exception) {
                Log.e("OnboardingVM", "Google sign-in failed: ${e.message}", e)
            } finally {
                _isSyncing.update { false }
                onComplete()
            }
        }
    }
}