package com.venom.ui.components.onboarding

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.resources.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreens(
    onGetStarted: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    val pages = getPages()
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        FloatingOrbs(pages[pagerState.currentPage])

        Column(modifier = Modifier.fillMaxSize()) {
            TopSection(
                showSkip = pagerState.currentPage < pages.size - 1,
                onSkip = { scope.launch { pagerState.animateScrollToPage(pages.size - 1) }; onSkip() }
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                PageContent(
                    page = pages[pageIndex],
                    onNext = {
                        scope.launch {
                            if (pageIndex < pages.size - 1) {
                                pagerState.animateScrollToPage(pageIndex + 1)
                            } else {
                                onGetStarted()
                            }
                        }
                    },
                    isLastPage = pageIndex == pages.size - 1
                )
            }
            PageIndicators(
                currentPage = pagerState.currentPage,
                totalPages = pages.size,
                onPageClick = { page ->
                    scope.launch {
                        pagerState.animateScrollToPage(page)
                    }
                },
            )
        }
    }
}

@Composable
private fun TopSection(
    showSkip: Boolean,
    onSkip: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.End
    ) {
        if (showSkip) {
            TextButton(onClick = onSkip) {
                Text(
                    text = stringResource(id = R.string.skip),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun PageContent(
    page: OnboardingPage,
    onNext: () -> Unit,
    isLastPage: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HeroSection(page)

        Spacer(modifier = Modifier.height(60.dp))

        ContentSection(page)

        Spacer(modifier = Modifier.weight(1f))

        ActionButton(
            text = if (isLastPage) stringResource(R.string.get_started) else stringResource(R.string.next),
            onClick = onNext,
            colors = listOf(page.primaryColor, page.secondaryColor)
        )
    }
}

@Preview()
@Composable
private fun OnboardingPreview() {
    OnboardingScreens()
}