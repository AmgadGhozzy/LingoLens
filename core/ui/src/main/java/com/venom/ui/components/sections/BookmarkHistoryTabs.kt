package com.venom.ui.components.sections

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R

/**
 * A segmented button group component for switching between Bookmarks and History views.
 * Implements Material 3 design guidelines for toggle buttons.
 *
 * @param selectedTab Current selected tab index (0 for Bookmarks, 1 for History)
 * @param onTabSelected Callback invoked when a tab is selected
 * @param modifier Optional modifier for the component
 */
@Composable
fun BookmarkHistoryTabs(
    selectedTab: Int, onTabSelected: (Int) -> Unit, modifier: Modifier = Modifier
) {
    // Define tabs statically to avoid recreations
    val tabs = listOf(
        R.string.bookmarks_title to R.drawable.icon_bookmark_outline,
        R.string.history_title to R.drawable.icon_history
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEachIndexed { index, (titleRes, iconRes) ->
                val isSelected = selectedTab == index
                val alpha by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0.6f, label = "Tab Alpha"
                )

                TabItem(
                    title = stringResource(titleRes),
                    icon = iconRes,
                    isSelected = isSelected,
                    alpha = alpha,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TabItem(
    title: String,
    icon: Int,
    isSelected: Boolean,
    alpha: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface.copy(
            alpha = 0.2f
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .alpha(alpha),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = title,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarkHistoryTabsPreview() {
    MaterialTheme {
        BookmarkHistoryTabs(
            selectedTab = 0, onTabSelected = {}, modifier = Modifier.padding(16.dp)
        )
    }
}