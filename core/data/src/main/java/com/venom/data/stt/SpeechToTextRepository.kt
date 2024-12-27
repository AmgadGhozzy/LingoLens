package com.venom.data.stt

import com.venom.domain.model.SpeechState
import com.venom.domain.repo.stt.ISpeechToTextRepository
import com.venom.domain.repo.stt.SpeechConfig
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechToTextRepository @Inject constructor(
    private val speechToTextManager: SpeechToTextManager
) : ISpeechToTextRepository {
    override val state: StateFlow<SpeechState> = speechToTextManager.state

    override suspend fun startListening(config: SpeechConfig) =
        speechToTextManager.startListening(config)

    override suspend fun pauseListening() = speechToTextManager.pauseListening()

    override suspend fun stopListening() = speechToTextManager.stopListening()

    override suspend fun cancelListening() = speechToTextManager.cancelListening()

    override suspend fun destroy() = speechToTextManager.destroy()
}
