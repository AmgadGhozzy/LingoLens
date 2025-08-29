package com.venom.ui.components.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.buttons.ExpandIndicator
import com.venom.ui.components.other.GlassCard

@Composable
fun ExpandableCard(
    title: String,
    expanded: Boolean? = null,
    onExpandChange: ((Boolean) -> Unit)? = null,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    onBookmark: (() -> Unit)? = null,
    expandedContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {

    var internalExpanded by remember { mutableStateOf(false) }
    val isExpanded = expanded ?: internalExpanded
    GlassCard(
        modifier = modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        onClick = {
            val newExpanded = !isExpanded
            if (expanded == null) {
                internalExpanded = newExpanded
            }
            onExpandChange?.invoke(newExpanded)
        },
        solidBackgroundAlpha = 0.3f
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

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    expandedContent()
                }
            }

            ExpandIndicator(
                expanded = isExpanded,
                onClick = {
                    val newExpanded = !isExpanded
                    if (expanded == null) {
                        internalExpanded = newExpanded
                    }
                    onExpandChange?.invoke(newExpanded)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}