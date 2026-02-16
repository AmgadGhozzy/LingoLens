package com.venom.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.venom.resources.R
import com.venom.ui.components.buttons.GlassOutlinedButton
import com.venom.ui.components.buttons.GradientActionButton
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp
import com.venom.ui.theme.LingoLensTheme

@Composable
fun PlacementChoiceDialog(
    onStartFromBeginning: () -> Unit,
    onTakePlacementTest: () -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.adp),
            color = colorScheme.surface,
            tonalElevation = 6.adp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.adp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome! 🎓",
                    fontSize = 22.asp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(Modifier.height(8.adp))

                Text(
                    text = "How would you like to start your learning journey?",
                    fontSize = 14.asp,
                    color = colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.adp))
                GradientActionButton(
                    text = "Take Placement Test",
                    onClick = onTakePlacementTest,
                    leadingIcon = R.drawable.ic_question_bold
                )

                Spacer(Modifier.height(8.adp))

                Text(
                    text = "Answer 6 quick questions to find your level",
                    fontSize = 12.asp,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.adp))

                GlassOutlinedButton(
                    text = "Start from Beginning",
                    onClick = onStartFromBeginning,
                    leadingIcon = R.drawable.icon_play
                )

                Spacer(Modifier.height(4.adp))

                Text(
                    text = "Start at Beginner level (A1)",
                    fontSize = 12.asp,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlacementChoiceDialogPreview() {
    LingoLensTheme(){
        PlacementChoiceDialog(
            onStartFromBeginning = {},
            onTakePlacementTest = {},
            onDismiss = {}
        )
    }
}
