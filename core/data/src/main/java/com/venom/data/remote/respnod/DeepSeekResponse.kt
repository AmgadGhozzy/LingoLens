package com.venom.data.remote.respnod

import com.google.gson.annotations.SerializedName

data class DeepSeekResponse(
    val id: String? = null,
    @SerializedName("object")
    val objectName: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val usage: DeepSeekUsage? = null,
    val choices: List<DeepSeekChoice>? = null
)

data class DeepSeekUsage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int? = null,
    @SerializedName("completion_tokens")
    val completionTokens: Int? = null,
    @SerializedName("total_tokens")
    val totalTokens: Int? = null
)

data class DeepSeekChoice(
    val message: DeepSeekMessage? = null,
    @SerializedName("finish_reason")
    val finishReason: String? = null,
    val index: Int? = null
)

data class DeepSeekMessage(
    val role: String? = null,
    val content: String? = null
)