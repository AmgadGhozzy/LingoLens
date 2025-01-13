package com.venom.lingolens.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.venom.ui.components.dialogs.CustomCard
import com.venom.ui.navigation.Screen

@Composable
fun LingoLensBottomBar(
    navController: NavHostController, selectedScreen: Screen, onScreenSelected: (Screen) -> Unit
) {
    CustomCard {
        NavigationBar(modifier = Modifier.height(72.dp)) {
            NavigationItems.entries.forEach { item ->
                NavigationBarItem(icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = stringResource(item.titleRes)
                    )
                }, selected = selectedScreen == item.screen, onClick = {
                    onScreenSelected(item.screen)
                    navController.navigateToStart(item.screen)
                }, alwaysShowLabel = false
                )
            }
        }
    }
}