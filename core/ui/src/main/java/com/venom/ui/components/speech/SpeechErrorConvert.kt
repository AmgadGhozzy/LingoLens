package com.venom.data.mapper

import com.venom.domain.model.SpeechError
import com.venom.resources.R

fun SpeechError.toErrorMessage(): Int = when (this) {
    SpeechError.NO_PERMISSION -> R.string.speech_error_no_permission
    SpeechError.NOT_AVAILABLE -> R.string.speech_error_not_available
    SpeechError.AUDIO -> R.string.speech_error_audio
    SpeechError.NETWORK -> R.string.speech_error_network
    SpeechError.TIMEOUT -> R.string.speech_error_timeout
    SpeechError.NO_MATCH -> R.string.speech_error_no_match
    SpeechError.BUSY -> R.string.speech_error_busy
    SpeechError.SERVER -> R.string.speech_error_server
    SpeechError.NO_INPUT -> R.string.speech_error_no_input
    SpeechError.UNKNOWN -> R.string.speech_error_unknown
}