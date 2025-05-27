package com.venom.data.model

import com.google.gson.annotations.SerializedName

data class ChatGPTResponse(
    val id: String? = null,
    @SerializedName("object")
    val objectName: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val usage: ChatGPTUsage? = null,
    val choices: List<ChatGPTChoice>? = null
)

data class ChatGPTUsage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int? = null,
    @SerializedName("completion_tokens")
    val completionTokens: Int? = null,
    @SerializedName("total_tokens")
    val totalTokens: Int? = null
)

data class ChatGPTChoice(
    val message: ChatGPTMessage? = null,
    @SerializedName("finish_reason")
    val finishReason: String? = null,
    val index: Int? = null
)

data class ChatGPTMessage(
    val role: String? = null,
    val content: String? = null
)