package com.venom.textsnap.ui.components.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.common.adp
import com.venom.ui.components.dialogs.FullscreenTextDialog
import com.venom.ui.components.inputs.CustomTextField
import com.venom.ui.components.other.GlassCard
import com.venom.ui.components.other.GlassThickness

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
    val screenHeight = LocalConfiguration.current.screenHeightDp
    var fullscreenText by remember { mutableStateOf<String?>(null) }

    val currentHeight = if (isExpanded) {
        (screenHeight * 0.3f).adp.coerceIn(112.adp, 224.adp)
    } else {
        (peekHeight * 0.6f).coerceIn(56.adp, 112.adp)
    }

    fullscreenText?.takeIf { it.isNotEmpty() }?.let {
        FullscreenTextDialog(
            text = it,
            onDismiss = { fullscreenText = null },
            onCopy = onCopy,
            onShare = onShare,
            onSpeak = onSpeak
        )
    }

    Box {
        GlassCard(thickness = GlassThickness.Regular) {
            SelectionContainer {
                CustomTextField(
                    textValue = TextFieldValue(text),
                    placeHolderText = stringResource(R.string.recognized_text_placeholder),
                    maxHeight = currentHeight,
                    minHeight = currentHeight,
                    isReadOnly = true
                )
            }
        }

        if (text.isNotEmpty()) {
            CustomButton(
                icon = R.drawable.icon_fullscreen,
                contentDescription = stringResource(R.string.action_fullscreen),
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = { fullscreenText = text },
                showBorder = false
            )
        }
    }
}