package com.venom.ui.components.sections

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import com.venom.resources.R
import com.venom.ui.components.common.adp
import com.venom.ui.components.other.GlassCard

data class TabItem(val titleRes: Int, val iconRes: Int)

@Composable
fun CustomTabs(
    tabs: List<TabItem>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier, shape = RoundedCornerShape(20.adp)) {
        Row(
            modifier = Modifier.padding(vertical = 3.adp, horizontal = 4.adp),
            horizontalArrangement = Arrangement.spacedBy(4.adp)
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    title = stringResource(tab.titleRes),
                    icon = tab.iconRes,
                    isSelected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun Tab(
    title: String,
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val animSpec = tween<Float>(250)
    val alpha by animateFloatAsState(if (isSelected) 1f else 0.6f, animSpec)
    val bgColor by animateColorAsState(
        if (isSelected) colors.primaryContainer.copy(0.6f)
        else colors.surface.copy(0.2f),
        tween(250)
    )
    val textColor by animateColorAsState(
        if (isSelected) colors.onPrimaryContainer else colors.onSurface,
        tween(250)
    )

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.adp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.adp, vertical = 8.adp)
                .alpha(alpha),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(18.adp),
                tint = colors.onSurface
            )
            Text(
                text = title,
                color = textColor,
                modifier = Modifier.padding(start = 8.adp),
                style = MaterialTheme.typography.labelLarge
            )
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
            modifier = Modifier.padding(16.adp)
        )
    }
}