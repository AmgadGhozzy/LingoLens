package com.venom.lingopro.ui.components.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.TextFieldValue
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.common.PulseAnimation
import com.venom.ui.components.common.adp
import com.venom.ui.components.inputs.CustomTextField

@Composable
fun SourceTextSection(
    sourceTextValue: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    onClearText: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Source text input area"
            }
    ) {
        CustomTextField(
            textValue = sourceTextValue,
            onTextChange = onTextChange,
            placeHolderText = stringResource(R.string.translate_type_text),
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.adp)
                .semantics {
                    contentDescription = "Enter text to translate"
                }
        )

        AnimatedVisibility(
            visible = sourceTextValue.text.isNotEmpty(),
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300))
        ) {
            Box(
                modifier = Modifier
                    .size(48.adp)
                    .semantics {
                        liveRegion = LiveRegionMode.Polite
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    PulseAnimation(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.semantics {
                            contentDescription = "Translation in progress"
                        }
                    )
                } else {
                    CustomButton(
                        icon = R.drawable.icon_clear,
                        contentDescription = stringResource(R.string.action_clear),
                        onClick = onClearText,
                        iconSize = 18.adp,
                        showBorder = false,
                        modifier = Modifier.semantics {
                            contentDescription = "Clear input text"
                        }
                    )
                }
            }
        }
    }
}