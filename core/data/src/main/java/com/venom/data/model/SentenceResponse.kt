package com.venom.data.model

import com.google.gson.annotations.SerializedName

data class SentenceResponse(
    val word: String,
    val sentences: List<String>,
    val sources: List<String>,
    @SerializedName("total_sentences")
    val totalSentences: Int,
    @SerializedName("returned_sentences")
    val returnedSentences: Int,
    val limit: Int
)

sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val message: String) : NetworkResult<T>()
    data class Loading<T>(val isLoading: Boolean = true) : NetworkResult<T>()
}