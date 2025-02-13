package com.venom.data.repo

import com.venom.data.api.GithubApi
import com.venom.data.model.UpdateConfig
import javax.inject.Inject

class UpdateChecker @Inject constructor(
    private val api: GithubApi
) {
    suspend fun checkForUpdates(): UpdateConfig {
        return try {
            api.getConfig()
        } catch (e: Exception) {
            e.printStackTrace()
            UpdateConfig()
        }
    }
}
