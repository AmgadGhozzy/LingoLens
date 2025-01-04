package com.venom.dialog.ui.component.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun VoiceInputControls(
    onSourceLanguageClick: () -> Unit,
    onTargetLanguageClick: () -> Unit,
    onClearHistory: () -> Unit,
    sourceLanguage: String = "English",
    targetLanguage: String = "العربية",
    modifier: Modifier = Modifier
) {
    val darkBackground = Color(0xFF121212)
    val buttonBackground = Color(0xFF1E1E1E)
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(darkBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Clear history button
        Button(
            onClick = onClearHistory,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonBackground
            ),
            modifier = Modifier
                .padding(bottom = 32.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Text(
                text = "Clear history",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
        }

        // Language selection buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Source language
            LanguageButton(
                language = sourceLanguage,
                onClick = onSourceLanguageClick,
                backgroundColor = Color(0xFF0D4A42)
            )

            // Target language
            LanguageButton(
                language = targetLanguage,
                onClick = onTargetLanguageClick,
                backgroundColor = Color(0xFF1A237E)
            )
        }
    }
}

@Composable
private fun LanguageButton(
    language: String,
    onClick: () -> Unit,
    backgroundColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(80.dp)
                .background(backgroundColor, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Rounded.Mic,
                contentDescription = "Voice input for $language",
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = language,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Preview
@Composable
fun VoiceInputControlsPreview() {
    VoiceInputControls(
        onSourceLanguageClick = {},
        onTargetLanguageClick = {},
        onClearHistory = {}
    )
}
