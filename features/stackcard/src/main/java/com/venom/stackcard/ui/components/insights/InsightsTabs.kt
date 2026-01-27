package com.venom.stackcard.ui.components.insights

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.stackcard.ui.viewmodel.InsightsTab
import com.venom.ui.theme.LingoLensTheme

/**
 * Tab navigation bar for the Insights bottom sheet.
 *
 * Displays a segmented control with 4 tabs:
 * Overview, Relations, Usage, Languages
 *
 * @param activeTab Currently selected tab
 * @param onTabChange Callback when a tab is selected
 * @param modifier Modifier for styling
 */
@Composable
fun InsightsTabs(
    activeTab: InsightsTab,
    onTabChange: (InsightsTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        InsightsTab.entries.forEach { tab ->
            val isActive = activeTab == tab

            val backgroundColor by animateColorAsState(
                targetValue = if (isActive) {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                },
                animationSpec = tween(200)
            )

            val textColor by animateColorAsState(
                targetValue = if (isActive) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                animationSpec = tween(200)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor)
                    .clickable { onTabChange(tab) }
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp
                    ),
                    color = textColor,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun InsightsTabsPreview() {
    LingoLensTheme {
        InsightsTabs(
            activeTab = InsightsTab.OVERVIEW,
            onTabChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun InsightsTabsRelationsPreview() {
    LingoLensTheme {
        InsightsTabs(
            activeTab = InsightsTab.RELATIONS,
            onTabChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
