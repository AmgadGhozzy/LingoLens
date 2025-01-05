package com.venom.dialog.ui.component.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.dialog.ui.component.LanguageButton
import com.venom.dialog.ui.component.MicButton

@Composable
fun VoiceInputControls(
    onSourceLanguageClick: () -> Unit,
    onTargetLanguageClick: () -> Unit,
    sourceLanguage: String,
    targetLanguage: String,
    isListening: Boolean,
    onMicClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MicButton(
                    isListening = isListening,
                    onClick = onMicClick,
                    containerColor = MaterialTheme.colorScheme.primary
                )
                LanguageButton(
                    language = sourceLanguage,
                    onClick = onSourceLanguageClick,
                    backgroundColor = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MicButton(
                    isListening = isListening,
                    onClick = onMicClick,
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
                LanguageButton(
                    language = targetLanguage,
                    onClick = onTargetLanguageClick,
                    backgroundColor = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}


@Preview
@Composable
fun VoiceInputControlsPreview() {
    VoiceInputControls(
        onSourceLanguageClick = {},
        onTargetLanguageClick = {},
        sourceLanguage = "English",
        targetLanguage = "Arabic",
        isListening = false,
        onMicClick = {}
    )
}
