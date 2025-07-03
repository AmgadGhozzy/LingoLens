package com.venom.lingolens.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.venom.lingolens.navigation.NavigationItems
import com.venom.lingolens.navigation.navigateToStart
import com.venom.ui.navigation.Screen

@Composable
fun LingoLensBottomBar(
    navController: NavHostController,
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                containerColor = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                NavigationItems.entries.filter { it.showInBottomBar }.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1f,
                        animationSpec = tween(300)
                    )
                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        animationSpec = tween(300)
                    )

                    NavigationBarItem(
                        icon = {

                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = stringResource(item.titleRes),
                                tint = iconColor,
                                modifier = Modifier
                                    .size(28.dp)
                                    .scale(scale)
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            onScreenSelected(item.screen)
                            navController.navigateToStart(item.screen)
                        },
                        alwaysShowLabel = false,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = 0.8f
                            ),
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}