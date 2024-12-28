package com.venom.domain.model

sealed class SpeechState {
    data object Idle : SpeechState()
    data object Listening : SpeechState()
    data class Paused(val lastResult: String) : SpeechState()
    data class Partial(val text: String) : SpeechState()
    data class Result(val text: String) : SpeechState()
    data class Error(val error: SpeechError) : SpeechState()
}
