package com.venom.ui.components.common

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.buttons.ExpandIndicator

@Composable
fun ExpandableCard(
    title: String,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    onBookmark: (() -> Unit)? = null,
    expandedContent: @Composable ColumnScope.() -> Unit,
    onExpandChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        onClick = {
            expanded = !expanded
            onExpandChange?.invoke(expanded)
        },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DynamicStyledText(
                    text = title,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Default actions
                    CustomFilledIconButton(
                        icon = R.drawable.icon_sound,
                        shape = MaterialTheme.shapes.small,
                        isAlpha = true,
                        contentDescription = stringResource(R.string.action_speak),
                        onClick = { onSpeak(title) }
                    )
                    CustomFilledIconButton(
                        icon = R.drawable.icon_copy,
                        shape = MaterialTheme.shapes.small,
                        isAlpha = true,
                        contentDescription = stringResource(R.string.action_copy),
                        onClick = { onCopy(title) }
                    )
                    onBookmark?.let {
                        CustomFilledIconButton(
                            icon = R.drawable.icon_bookmark_filled,
                            shape = MaterialTheme.shapes.small,
                            isAlpha = true,
                            contentDescription = stringResource(R.string.bookmark_remove),
                            onClick = onBookmark
                        )
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    expandedContent()
                }
            }

            ExpandIndicator(
                expanded = expanded,
                onClick = {
                    expanded = !expanded
                    onExpandChange?.invoke(expanded)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}