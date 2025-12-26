package com.venom.lingolens.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.venom.lingolens.navigation.NavigationItems
import com.venom.lingolens.navigation.navigateToStart
import com.venom.ui.components.other.GlassCard
import com.venom.ui.navigation.Screen
import com.venom.ui.theme.ThemeColors.GlassPrimary
import com.venom.ui.theme.ThemeColors.GlassSecondary
import com.venom.ui.theme.ThemeColors.GlassTertiary

@Composable
fun LingoLensBottomBar(
    navController: NavHostController,
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    RoundedCornerShape(20.dp)
    val itemShape = RoundedCornerShape(14.dp)
    val visibleItems = NavigationItems.entries.filter { it.showInBottomBar }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .scale(0.95f)
            .offset(y = (-8).dp),
        solidBackgroundAlpha = 0.9f
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            visibleItems.forEach { item ->
                val isSelected = currentScreen == item.screen

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .then(
                            if (isSelected) Modifier
                                .border(
                                    0.8.dp,
                                    MaterialTheme.colorScheme.primary.copy(0.1f),
                                    RoundedCornerShape(18.dp)
                                )
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            GlassPrimary.copy(0.05f),
                                            GlassSecondary.copy(0.05f),
                                            GlassTertiary.copy(0.05f)
                                        )
                                    ),
                                    RoundedCornerShape(18.dp)
                                )
                                .shadow(
                                    elevation = 24.dp,
                                    shape = itemShape,
                                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            else Modifier
                        )
                        .clip(itemShape)
                        .clickable {
                            onScreenSelected(item.screen)
                            navController.navigateToStart(item.screen)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = stringResource(item.titleRes),
                        tint = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}