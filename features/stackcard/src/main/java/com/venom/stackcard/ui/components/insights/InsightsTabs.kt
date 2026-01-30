package com.venom.stackcard.ui.components.insights

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.domain.model.AppTheme
import com.venom.ui.theme.LingoLensTheme

/**
 * Tab navigation bar for the Insights bottom sheet.
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
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        InsightsTab.entries.forEach { tab ->
            TabItem(
                tab = tab,
                isActive = activeTab == tab,
                onClick = { onTabChange(tab) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TabItem(
    tab: InsightsTab,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.surfaceContainerHighest
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        animationSpec = tween(durationMillis = 150)
    )

    val textColor by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 150)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 12.dp, horizontal = 10.dp),
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


enum class InsightsTab(val title: String) {
    OVERVIEW("Overview"),
    RELATIONS("Relations"),
    USAGE("Usage"),
    LANGUAGES("Languages")
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun InsightsTabsPreview() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        InsightsTabs(
            activeTab = InsightsTab.OVERVIEW,
            onTabChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F5F9)
@Composable
private fun InsightsTabsPreviewLight() {
    LingoLensTheme(appTheme = AppTheme.LIGHT) {
        InsightsTabs(
            activeTab = InsightsTab.OVERVIEW,
            onTabChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}