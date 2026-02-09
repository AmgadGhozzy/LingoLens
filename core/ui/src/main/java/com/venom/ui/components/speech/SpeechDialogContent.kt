package com.venom.ui.components.speech

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.ui.components.common.adp
import com.venom.ui.viewmodel.STTViewModel

@Composable
fun SpeechDialogContent(
    sttViewModel: STTViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val speechState by sttViewModel.speechState.collectAsState()
    val transcription by sttViewModel.transcription.collectAsState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.adp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.adp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.adp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.adp)
            ) {
                StateHeader(speechState, onDismiss)
                AnimatedSpeechVisualizer(speechState)

                TranscriptionArea(speechState = speechState, transcription = transcription)

                ControlPanel(
                    state = speechState,
                    onStop = sttViewModel::stopRecognition,
                    onStart = sttViewModel::startRecognition,
                    onPause = sttViewModel::pauseRecognition,
                    onResume = sttViewModel::startRecognition
                )
            }
        }
    }
}
