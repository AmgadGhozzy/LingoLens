package com.venom.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.venom.resources.R
import com.venom.ui.components.buttons.GradientActionButton
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp
import com.venom.ui.components.onboarding.ContentSection
import com.venom.ui.components.onboarding.GoogleSignInButton
import com.venom.ui.components.onboarding.HeroSection
import com.venom.ui.components.onboarding.OnboardingPage
import com.venom.ui.components.onboarding.PageIndicators
import com.venom.ui.components.onboarding.onboardingPages
import com.venom.ui.components.other.FloatingOrbs
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreens(
    onGetStarted: () -> Unit = {},
    onSkip: () -> Unit = {},
    onGoogleSignIn: () -> Unit = {}
) {
    val pages = onboardingPages()
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val currentPage = pages[pagerState.currentPage]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .systemBarsPadding()
    ) {
        FloatingOrbs(
            primaryColor = currentPage.colors.primary,
            secondaryColor = currentPage.colors.secondary
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.adp),
                horizontalArrangement = Arrangement.End
            ) {
                if (pagerState.currentPage < pages.size - 1) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pages.size - 1)
                            }
                            onSkip()
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.adp))
                            .background(Color.White.copy(0.1f))
                    ) {
                        Text(
                            text = stringResource(R.string.skip),
                            fontSize = 16.asp,
                            fontWeight = FontWeight.Medium,
                            color = currentPage.colors.primary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.adp))
                }
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                val pageOffset = (pagerState.currentPage - pageIndex) +
                        pagerState.currentPageOffsetFraction

                PageContent(
                    page = pages[pageIndex],
                    pageOffset = pageOffset,
                    onNext = {
                        scope.launch {
                            if (pageIndex < pages.size - 1) {
                                pagerState.animateScrollToPage(pageIndex + 1)
                            } else {
                                onGetStarted()
                            }
                        }
                    },
                    onGoogleSignIn = onGoogleSignIn,
                    isLastPage = pageIndex == pages.size - 1
                )
            }

            // Page Indicators
            PageIndicators(
                currentPage = pagerState.currentPage,
                totalPages = pages.size,
                onPageClick = { page ->
                    scope.launch { pagerState.animateScrollToPage(page) }
                }
            )
        }
    }
}

@Composable
private fun PageContent(
    page: OnboardingPage,
    pageOffset: Float,
    onNext: () -> Unit,
    onGoogleSignIn: () -> Unit,
    isLastPage: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.adp)
            .graphicsLayer {
                // Scale and fade animation during transitions
                val scale = 1f - (pageOffset.absoluteValue * 0.1f)
                scaleX = scale.coerceIn(0.85f, 1f)
                scaleY = scale.coerceIn(0.85f, 1f)
                1f - (pageOffset.absoluteValue * 0.3f)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HeroSection(
            page = page
        )

        Spacer(modifier = Modifier.height(60.adp))

        ContentSection(page = page)

        Spacer(modifier = Modifier.weight(1f))

        GradientActionButton(
            text = if (isLastPage) {
                stringResource(R.string.get_started)
            } else {
                stringResource(R.string.next)
            },
            onClick = onNext,
            gradientColors = listOf(page.colors.primary, page.colors.secondary),
            shadowColors = listOf(
                page.colors.primary.copy(alpha = 0.5f),
                page.colors.secondary.copy(alpha = 0.5f)
            ),
            maxWidth = 600,
            modifier = Modifier.padding(bottom = if (isLastPage) 8.adp else 16.adp)
        )

        if (isLastPage) {
            GoogleSignInButton(
                onClick = onGoogleSignIn,
                modifier = Modifier.padding(bottom = 16.adp)
            )
        }
    }
}