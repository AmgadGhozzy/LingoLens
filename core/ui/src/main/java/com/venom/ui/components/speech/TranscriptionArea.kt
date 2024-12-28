package com.venom.ui.components.speech

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.data.mapper.toErrorMessage
import com.venom.domain.model.SpeechState
import com.venom.ui.components.common.ErrorMessage

@Composable
fun TranscriptionArea(
    speechState: SpeechState,
    transcription: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Current recognition
        AnimatedContent(
            targetState = speechState,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            }, label = "Speech Transition"
        ) { currentState ->
            when (currentState) {
                is SpeechState.Idle -> Spacer(modifier = Modifier.height(56.dp))
                is SpeechState.Partial -> RecognitionBubble(
                    text = currentState.text,
                    isPartial = true
                )

                is SpeechState.Result -> RecognitionBubble(
                    text = currentState.text,
                    isPartial = false
                )

                is SpeechState.Error -> ErrorMessage(
                    stringResource(currentState.error.toErrorMessage())
                )

                is SpeechState.Listening,
                is SpeechState.Paused -> Spacer(modifier = Modifier.height(56.dp))
            }
        }

        // Transcript
        if (transcription.isNotEmpty()) {
            TranscriptCard(transcription)
        }
    }
}

@Preview
@Composable
fun TranscriptionAreaPreview() {
    TranscriptionArea(SpeechState.Idle, "")
}
