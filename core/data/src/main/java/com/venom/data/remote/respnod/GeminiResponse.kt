package com.venom.data.remote.respnod

import com.google.gson.annotations.SerializedName

data class GeminiResponse(
    val candidates: List<GeminiCandidate> = emptyList(),
    @SerializedName("promptFeedback")
    val promptFeedback: GeminiPromptFeedback? = null,
    @SerializedName("usageMetadata")
    val usageMetadata: GeminiUsageMetadata? = null
)

data class GeminiCandidate(
    val content: GeminiContent? = null,
    @SerializedName("finishReason")
    val finishReason: String? = null,
    val index: Int = 0,
    @SerializedName("safetyRatings")
    val safetyRatings: List<GeminiSafetyRating> = emptyList()
)

data class GeminiContent(
    val parts: List<GeminiPart> = emptyList(),
    val role: String = "model"
)

data class GeminiPart(
    val text: String = ""
)

data class GeminiPromptFeedback(
    @SerializedName("safetyRatings")
    val safetyRatings: List<GeminiSafetyRating> = emptyList()
)

data class GeminiSafetyRating(
    val category: String = "",
    val probability: String = ""
)

data class GeminiUsageMetadata(
    @SerializedName("promptTokenCount")
    val promptTokenCount: Int = 0,
    @SerializedName("candidatesTokenCount")
    val candidatesTokenCount: Int = 0,
    @SerializedName("totalTokenCount")
    val totalTokenCount: Int = 0
)