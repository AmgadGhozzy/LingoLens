package com.venom.stackcard.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.venom.ui.components.common.PulseAnimation
import com.venom.ui.components.other.TextChange

@Composable
fun CardText(
    text: String,
    wordTerms: String?,
    isLoading: Boolean,
    isFlipped: Boolean
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Main text - always displayed
        TextChange(text)

        // Fixed height spacer to maintain consistent spacing
        Spacer(modifier = Modifier.height(24.dp))

        // Translation area - reserve space even when empty
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp), // Fixed height for translation area
            contentAlignment = Alignment.Center
        ) {
            if (isFlipped) {
                when {
                    isLoading -> PulseAnimation(size = 32f)
                    wordTerms != null && wordTerms.isNotEmpty() -> {
                        TextChange(
                            text = wordTerms,
                            textAlign = TextAlign.Center
                        )
                    }
                    // When flipped but no translation yet, show nothing but keep the space
                }
            }
        }
    }
}