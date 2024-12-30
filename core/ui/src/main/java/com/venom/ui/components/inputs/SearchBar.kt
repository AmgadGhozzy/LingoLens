package com.venom.ui.components.inputs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton

/**
 * A customizable search bar component with enhanced features.
 *
 * @param searchQuery The current search query text
 * @param onSearchQueryChanged Callback when search query is modified
 * @param onSearchTriggered Callback when search is triggered (via keyboard or search icon)
 * @param modifier Additional modifier for customization
 * @param searchHint Placeholder text when search query is empty
 * @param enabled Whether the search bar is interactive
 * @param searchIconTint Color of the search icon
 * @param clearIconTint Color of the clear icon
 */
@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: () -> Unit = {},
    modifier: Modifier = Modifier,
    searchHint: String = stringResource(R.string.nav_search),
    enabled: Boolean = true,
    searchIconTint: Color = MaterialTheme.colorScheme.primary,
    clearIconTint: Color = MaterialTheme.colorScheme.error,
    shape: RoundedCornerShape = RoundedCornerShape(28.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .semantics { contentDescription = searchHint },
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (isFocused) 2.dp else 0.dp,
        shape = shape
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Search Icon
            CustomButton(
                icon = R.drawable.icon_search,
                selectedTint = searchIconTint,
                contentDescription = stringResource(R.string.nav_search),
                onClick = {
                    if (enabled) {
                        onSearchTriggered()
                        focusRequester.requestFocus()
                    }
                },
            )

            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged { isFocused = it.isFocused },
                    enabled = enabled,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = if (searchQuery.isNotEmpty()) ImeAction.Search else ImeAction.None
                    ),
                    keyboardActions = KeyboardActions(onSearch = {
                        onSearchTriggered()
                        keyboardController?.hide()
                    }),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    interactionSource = interactionSource,
                    visualTransformation = VisualTransformation.None,
                    decorationBox = { innerTextField ->
                        Box {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = searchHint,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            innerTextField()
                        }
                    })
            }

            AnimatedVisibility(
                visible = searchQuery.isNotEmpty() && enabled, enter = fadeIn(), exit = fadeOut()
            ) {
                CustomButton(
                    icon = R.drawable.icon_clear,
                    contentDescription = stringResource(R.string.action_clear),
                    selectedTint = clearIconTint,
                    onClick = {
                        onSearchQueryChanged("")
                        keyboardController?.hide()
                    },
                    enabled = enabled,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    MaterialTheme {
        SearchBar(
            searchQuery = "",
            onSearchQueryChanged = {},
            searchHint = "Search here...",
            modifier = Modifier.padding(16.dp)
        )
    }
}
