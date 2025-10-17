package com.venom.quote.ui.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.theme.QuoteColors

@Composable
fun QuoteTabs(
    selectedTab: QuoteTab,
    onTabSelected: (QuoteTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabShape = RoundedCornerShape(16.dp)
    val selectedTabShape = RoundedCornerShape(12.dp)

    // Colors for gradients and shadows
    val surfaceColor = QuoteColors.surfaceSecondary()
    val shadowColor = QuoteColors.primary()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(tabShape)
            .background(surfaceColor.copy(alpha = 0.6f))
            .border(
                1.dp,
                surfaceColor.copy(alpha = 0.3f),
                tabShape
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuoteTab.entries.toList().forEach { tab ->
            val isSelected = selectedTab == tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .then(
                        if (isSelected) {
                            Modifier.shadow(
                                elevation = 30.dp,
                                shape = selectedTabShape,
                                ambientColor = shadowColor.copy(alpha = 0.3f),
                                spotColor = shadowColor.copy(alpha = 0.5f)
                            )
                        } else Modifier
                    )
                    .clip(selectedTabShape)
                    .background(if (isSelected) surfaceColor else Color.Transparent)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) QuoteColors.border() else Color.Transparent,
                        shape = selectedTabShape
                    )
                    .clickable { onTabSelected(tab) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(tab.icon),
                    contentDescription = stringResource(id = tab.labelResId),
                    tint = if (isSelected) {
                        shadowColor
                    } else {
                        QuoteColors.textSecondary()
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


enum class QuoteTab(val labelResId: Int, @DrawableRes val icon: Int) {
    QUOTES(R.string.tab_all_quotes, R.drawable.icon_sparkles),
    FAVORITES(R.string.tab_favorites, R.drawable.icon_heart),
    AUTHORS(R.string.tab_authors, R.drawable.icon_user),
    TOPICS(R.string.tab_topics, R.drawable.icon_tag)
}

@Composable
fun NavigationTabsExample() {
    var selectedTab by remember { mutableStateOf(QuoteTab.QUOTES) }

    QuoteTabs(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it }
    )
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun NavigationTabsLightPreview() {
    MaterialTheme {
        NavigationTabsExample()
    }
}

@Preview(
    showBackground = true,
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun NavigationTabsDarkPreview() {
    MaterialTheme {
        NavigationTabsExample()
    }
}