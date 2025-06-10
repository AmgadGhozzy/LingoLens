package com.venom.ui.components.sections

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R

/**
 * Data class representing a tab item with a title and an icon resource.
 */
data class TabItem(
    val titleRes: Int,
    val iconRes: Int
)

/**
 * A segmented button group component for switching between different views.
 * Implements Material 3 design guidelines for toggle buttons.
 *
 * @param tabs List of tab items to be displayed
 * @param selectedTab Current selected tab index
 * @param onTabSelected Callback invoked when a tab is selected
 * @param modifier Optional modifier for the component
 */

@Composable
fun CustomTabs(
    tabs: List<TabItem>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        )
                    )
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                tabs.forEachIndexed { index, tabItem ->
                    val isSelected = selectedTab == index
                    val alpha by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.7f,
                        animationSpec = tween(300)
                    )

                    TabItemContent(
                        title = stringResource(tabItem.titleRes),
                        icon = tabItem.iconRes,
                        isSelected = isSelected,
                        alpha = alpha,
                        onClick = { onTabSelected(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomTabsPreview() {
    MaterialTheme {
        CustomTabs(
            tabs = listOf(
                TabItem(R.string.bookmarks_title, R.drawable.icon_bookmark_outline),
                TabItem(R.string.history_title, R.drawable.icon_history)
            ),
            selectedTab = 0,
            onTabSelected = {},
            modifier = Modifier.padding(18.dp)
        )
    }
}