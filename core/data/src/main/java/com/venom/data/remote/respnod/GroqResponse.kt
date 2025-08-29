package com.venom.data.remote.respnod

import com.google.gson.annotations.SerializedName

data class GroqResponse(
    val id: String? = null,
    @SerializedName("object")
    val objectName: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val usage: GroqUsage? = null,
    val choices: List<GroqChoice>? = null,
    @SerializedName("system_fingerprint")
    val systemFingerprint: String? = null
)

data class GroqUsage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int? = null,
    @SerializedName("completion_tokens")
    val completionTokens: Int? = null,
    @SerializedName("total_tokens")
    val totalTokens: Int? = null,
    @SerializedName("completion_time")
    val completionTime: Double? = null,
    @SerializedName("prompt_time")
    val promptTime: Double? = null,
    @SerializedName("queue_time")
    val queueTime: Double? = null,
    @SerializedName("total_time")
    val totalTime: Double? = null
)

data class GroqChoice(
    val message: GroqMessage? = null,
    @SerializedName("finish_reason")
    val finishReason: String? = null,
    val index: Int? = null,
    val logprobs: String? = null
)

data class GroqMessage(
    val role: String? = null,
    val content: String? = null
)