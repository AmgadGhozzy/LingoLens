package com.venom.ui.components.dialogs

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.venom.resources.R
import com.venom.ui.components.bars.TranslatedTextActionBar
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.inputs.CustomTextField
import com.venom.ui.components.other.GlassCard
import com.venom.ui.theme.LingoLensTheme
import com.venom.utils.Extensions.getSelectedOrFullText

/**
 * A fullscreen dialog for displaying text with additional actions
 *
 * @param textValue The text to be displayed in the dialog
 * @param onDismiss Callback triggered when the dialog is dismissed
 * @param onCopy Callback for copying the text
 * @param onShare Callback for sharing the text
 * @param onSpeak Callback for speaking the text
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenTextDialog(
    modifier: Modifier = Modifier,
    textValue: TextFieldValue,
    onDismiss: () -> Unit,
    onCopy: (String) -> Unit = {},
    onShare: (String) -> Unit = {},
    onSpeak: (String) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    var fontSize by remember { mutableFloatStateOf(24f) }
    var isEditable by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            dismissOnBackPress = true, usePlatformDefaultWidth = false
        )
    ) {
        GlassCard(solidBackgroundAlpha = 1f) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomFilledIconButton(
                        icon = Icons.Rounded.Edit,
                        onClick = { isEditable = !isEditable },
                        contentDescription = stringResource(R.string.action_edit),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.5f),
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        size = 38.dp
                    )
                    CustomFilledIconButton(
                        icon = Icons.Rounded.Close,
                        onClick = onDismiss,
                        contentDescription = stringResource(R.string.action_close),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.5f),
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        size = 38.dp
                    )
                }
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(0.1f),
                    tonalElevation = 2.dp
                ) {
                    CustomTextField(
                        textValue = textValue,
                        isReadOnly = !isEditable,
                        minFontSize = (fontSize * 0.8f).toInt(),
                        maxFontSize = (fontSize * 1.2f).toInt(),
                        maxLines = Int.MAX_VALUE,
                        minHeight = 200.dp,
                        maxHeight = 600.dp,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState())
                    )
                }

                Slider(
                    value = fontSize,
                    onValueChange = { fontSize = it },
                    valueRange = 16f..56f,
                    steps = 8,
                    thumb = {
                        SliderDefaults.Thumb(
                            interactionSource = interactionSource, modifier = modifier.height(24.dp)
                        )
                    },
                    track = {
                        SliderDefaults.Track(
                            sliderState = it, modifier = modifier.height(8.dp)
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                TranslatedTextActionBar(
                    onCopy = { onCopy(textValue.getSelectedOrFullText()) },
                    onShare = { onShare(textValue.getSelectedOrFullText()) },
                    onSpeak = { onSpeak(textValue.getSelectedOrFullText()) },
                    isSaved = false,
                    isSpeaking = false
                )
            }
        }
    }
}

@Preview
@Composable
fun FullscreenTextDialogPreview() {
    LingoLensTheme {
        FullscreenTextDialog(
            textValue = TextFieldValue("Hello"),
            onDismiss = { },
        )
    }
}