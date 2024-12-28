package com.venom.domain.model

data class SpeechConfig(
    val language: String = "en-US",
    val recognitionTimeoutMs: Long = 10000L,
    val retryDelayMs: Long = 1000L,
    val maxResults: Int = 1,
    val partialResults: Boolean = true,
    val completeSilenceLengthMs: Long = 3000L,
    val minimumLengthMs: Long = 500L,
    val possiblyCompleteSilenceLengthMs: Long = 1000L
)