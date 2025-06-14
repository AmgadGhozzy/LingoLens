package com.venom.textsnap.ui.components.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.dialogs.FullscreenTextDialog
import com.venom.ui.components.inputs.CustomTextField

@Composable
fun RecognizedTextSection(
    text: String,
    peekHeight: Dp,
    isExpanded: Boolean,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    var fullscreenState by remember { mutableStateOf<String?>(null) }

    val collapsedHeight = (peekHeight * 0.6f).coerceIn(56.dp, 112.dp)
    val expandedHeight = (configuration.screenHeightDp.dp * 0.3f).coerceIn(112.dp, 224.dp)
    val currentHeight = if (isExpanded) expandedHeight else collapsedHeight
    val maxLines = with(LocalDensity.current) {
        ((currentHeight.value / 18) * (configuration.densityDpi / 160f)).toInt()
    }

    fullscreenState?.takeIf { it.isNotEmpty() }?.let { fullscreenText ->
        FullscreenTextDialog(
            textValue = TextFieldValue(fullscreenText),
            onDismiss = { fullscreenState = null },
            onCopy = onCopy,
            onShare = onShare,
            onSpeak = onSpeak
        )
    }

    Box {
        SelectionContainer(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                )
        ) {
            CustomTextField(
                textValue = TextFieldValue(text),
                placeHolderText = stringResource(R.string.recognized_text_placeholder),
                maxLines = maxLines,
                maxHeight = currentHeight,
                minHeight = currentHeight,
                isReadOnly = true
            )
        }

        if (text.isNotEmpty()) {
            CustomButton(
                icon = R.drawable.icon_fullscreen,
                contentDescription = stringResource(R.string.action_fullscreen),
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = { fullscreenState = text }
            )
        }
    }
}