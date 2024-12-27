package com.venom.domain.repo.stt

data class SpeechConfig(
    val language: String = "en-US",
    val recognitionTimeoutMs: Long = 30000L,
    val retryDelayMs: Long = 1000L,
    val maxResults: Int = 1,
    val partialResults: Boolean = true
)