package com.venom.domain.repo.stt

import com.venom.domain.model.SpeechConfig
import com.venom.domain.model.SpeechState
import kotlinx.coroutines.flow.StateFlow

interface ISpeechToTextRepository {
    val state: StateFlow<SpeechState>

    suspend fun startListening(config: SpeechConfig = SpeechConfig())
    suspend fun pauseListening()
    suspend fun stopListening()
    suspend fun destroy()
}