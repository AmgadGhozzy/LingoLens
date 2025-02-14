package com.venom.data.api

import com.venom.data.model.UpdateConfig
import retrofit2.http.GET

interface GithubApi {
    @GET("config.json")
    suspend fun getConfig(): UpdateConfig
}
