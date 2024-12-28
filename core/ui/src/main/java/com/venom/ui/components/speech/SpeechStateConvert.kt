package com.venom.data.mapper

import com.venom.domain.model.SpeechState
import com.venom.resources.R

fun SpeechState.toDisplayString(): Int = when (this) {
    is SpeechState.Listening -> R.string.speech_state_listening
    is SpeechState.Partial -> R.string.speech_state_partial
    is SpeechState.Result -> R.string.speech_state_result
    is SpeechState.Error -> R.string.speech_state_error
    else -> R.string.speech_state_ready
}