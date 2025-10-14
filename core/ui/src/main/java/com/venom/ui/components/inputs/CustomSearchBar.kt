package com.venom.ui.components.inputs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.other.GlassCard

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
fun CustomSearchBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: () -> Unit = {},
    searchHint: String = stringResource(R.string.nav_search),
    enabled: Boolean = true,
    searchIconTint: Color = MaterialTheme.colorScheme.primary,
    clearIconTint: Color = MaterialTheme.colorScheme.error,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    GlassCard(modifier = modifier, padding = 8.dp, shape = shape) {
        Row(
            modifier = Modifier.padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CustomButton(
                icon = R.drawable.icon_search,
                selectedTint = searchIconTint,
                contentDescription = stringResource(R.string.nav_search),
                onClick = { if (enabled) onSearchTriggered() },
                showBorder = false
            )

            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier
                    .weight(1f),
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
                }
            )

            AnimatedVisibility(
                visible = searchQuery.isNotEmpty() && enabled,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CustomButton(
                    icon = R.drawable.icon_clear,
                    contentDescription = stringResource(R.string.action_clear),
                    selectedTint = clearIconTint,
                    onClick = {
                        onSearchQueryChanged("")
                        keyboardController?.hide()
                    },
                    showBorder = false,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun SearchBarPreview() {
    MaterialTheme {
        CustomSearchBar(
            searchQuery = "",
            onSearchQueryChanged = {},
            searchHint = "Search here...",
            modifier = Modifier.padding(16.dp)
        )
    }
}