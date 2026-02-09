package com.venom.ui.components.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.venom.resources.R
import com.venom.ui.components.bars.TranslatedTextActionBar
import com.venom.ui.components.buttons.CloseButton
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.common.adp
import com.venom.ui.components.inputs.CustomTextField
import com.venom.ui.components.other.GlassCard
import com.venom.ui.components.other.GlassThickness
import com.venom.ui.theme.LingoLensTheme
import com.venom.utils.Extensions.getSelectedOrFullText

/**
 * A fullscreen dialog for displaying and editing text with additional actions
 *
 * @param text The text to be displayed in the dialog
 * @param onDismiss Callback with the final text value (original if unchanged, modified if edited)
 * @param onCopy Callback for copying the text
 * @param onShare Callback for sharing the text
 * @param onSpeak Callback for speaking the text
 * @param allowEdit Whether editing is allowed (default true)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenTextDialog(
    text: String,
    onDismiss: (String) -> Unit,
    onCopy: (String) -> Unit = {},
    onShare: (String) -> Unit = {},
    onSpeak: (String) -> Unit = {},
    allowEdit: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    var fontSize by rememberSaveable { mutableFloatStateOf(24f) }
    var isEditing by rememberSaveable { mutableStateOf(true) }
    var textValue by remember { mutableStateOf(TextFieldValue(text)) }
    val originalText = remember { text }
    val hasChanges = textValue.text != originalText

    Dialog(
        onDismissRequest = { onDismiss(textValue.text) },
        properties = DialogProperties(
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        GlassCard(
            thickness = GlassThickness.UltraThick,
            showBorder = false
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.adp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.adp, vertical = 18.adp)
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.adp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (allowEdit) {
                            CustomFilledIconButton(
                                icon = if (isEditing) Icons.Rounded.Check else Icons.Rounded.Edit,
                                onClick = { isEditing = !isEditing },
                                contentDescription = stringResource(
                                    if (isEditing) R.string.action_done else R.string.action_edit
                                ),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = if (isEditing) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(0.5f)
                                    },
                                    contentColor = if (isEditing) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                ),
                                size = 38.adp
                            )

                            AnimatedVisibility(
                                visible = isEditing && hasChanges,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                CustomFilledIconButton(
                                    icon = Icons.AutoMirrored.Rounded.Undo,
                                    onClick = { textValue = TextFieldValue(originalText) },
                                    contentDescription = stringResource(R.string.action_undo),
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                            0.5f
                                        ),
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    size = 38.adp
                                )
                            }

                            AnimatedVisibility(
                                visible = isEditing,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(
                                    text = stringResource(R.string.editing_mode),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    CloseButton(
                        onClick = { onDismiss(textValue.text) }
                    )
                }

                // Text Display/Edit Area
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(20.adp),
                    color = if (isEditing) {
                        MaterialTheme.colorScheme.primaryContainer.copy(0.15f)
                    } else {
                        MaterialTheme.colorScheme.primaryContainer.copy(0.1f)
                    },
                    tonalElevation = if (isEditing) 4.adp else 2.adp
                ) {
                    CustomTextField(
                        textValue = textValue,
                        onTextChange = { textValue = it },
                        isReadOnly = !isEditing,
                        minFontSize = (fontSize * 0.8f).toInt(),
                        maxFontSize = (fontSize * 1.2f).toInt(),
                        maxLines = Int.MAX_VALUE,
                        minHeight = 200.adp,
                        maxHeight = 600.adp,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.adp)
                            .verticalScroll(rememberScrollState())
                    )
                }

                // Font Size Slider
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.adp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.font_size),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${fontSize.toInt()}sp",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Slider(
                        value = fontSize,
                        onValueChange = { fontSize = it },
                        valueRange = 16f..56f,
                        steps = 19,
                        thumb = {
                            SliderDefaults.Thumb(
                                interactionSource = interactionSource,
                                modifier = Modifier.height(24.adp)
                            )
                        },
                        track = {
                            SliderDefaults.Track(
                                sliderState = it,
                                modifier = Modifier.height(8.adp)
                            )
                        },
                        modifier = Modifier.padding(horizontal = 16.adp)
                    )
                }

                // Action Bar
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
            text = "Hello, this is a preview of the enhanced fullscreen text dialog.",
            onDismiss = {}
        )
    }
}