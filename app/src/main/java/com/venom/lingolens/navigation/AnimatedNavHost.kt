package com.venom.lingolens.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun AnimatedNavHost(
    navController: NavHostController,
    startDestination: String,
    navGraphBuilder: NavGraphBuilder.() -> Unit
) {
    NavHost(navController = navController, startDestination = startDestination, enterTransition = {
        // Entering animation for new screen
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Start,
            animationSpec = tween(
                durationMillis = 400, easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 250, delayMillis = 100, easing = LinearOutSlowInEasing
            )
        )
    }, exitTransition = {
        // Exiting animation for current screen
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Start,
            animationSpec = tween(
                durationMillis = 400, easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = 200, delayMillis = 50, easing = FastOutLinearInEasing
            )
        )
    }, popEnterTransition = {
        // Entering animation when popping back stack
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(
                durationMillis = 400, easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 250, delayMillis = 100, easing = LinearOutSlowInEasing
            )
        )
    }, popExitTransition = {
        // Exiting animation when popping back stack
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(
                durationMillis = 400, easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = 200, delayMillis = 50, easing = FastOutLinearInEasing
            )
        )
    }, builder = navGraphBuilder
    )
}
