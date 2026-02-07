package com.venom.stackcard.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.venom.domain.model.AppTheme
import com.venom.domain.model.UserProgressData
import com.venom.resources.R
import com.venom.stackcard.ui.components.mastery.MiniProgressDashboard
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.buttons.GradientActionButton
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp
import com.venom.ui.components.onboarding.GoogleSignInButton
import com.venom.ui.theme.BrandColors
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.theme.lingoLens
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onStart: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onGoogleSignIn: () -> Unit = {},
    onOpenProgress: () -> Unit = {},
    isLoading: Boolean = false,
    isSignedIn: Boolean = false,
    userName: String? = null,
    userProgress: UserProgressData? = null,
    modifier: Modifier = Modifier
) {
    var topic by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { delay(100); isVisible = true }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.95f,
        animationSpec = tween(400),
        label = "scale"
    )

    val displayName = userName?.takeIf { it.isNotBlank() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.adp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(
            isSignedIn = isSignedIn,
            userName = displayName,
            isLoading = isLoading,
            onBack = onBack
        )

        if (userProgress != null && isSignedIn) {
            DashboardSection(
                userProgress = userProgress,
                onOpenProgress = onOpenProgress,
                modifier = Modifier.weight(0.25f)
            )
        }

        Column(
            modifier = Modifier
                .weight(if (userProgress != null && isSignedIn) 0.30f else 0.45f)
                .scale(scale),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppLogo()
            Spacer(modifier = Modifier.height(20.adp))
            AppTitle()
        }

        Column(
            modifier = Modifier
                .weight(0.35f)
                .widthIn(max = 360.adp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.adp, Alignment.Top)
        ) {
            GradientActionButton(
                text = "Start Learning",
                onClick = { onStart(topic) },
                enabled = !isLoading,
                leadingIcon = R.drawable.icon_play,
                maxWidth = 360
            ) {
                if (isLoading) {
                    LottieAnimation(
                        composition = rememberLottieComposition(
                            LottieCompositionSpec.RawRes(R.raw.dot_loading)
                        ).value,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.fillMaxWidth(0.35f),
                        speed = 0.8f
                    )
                } else {
                    null
                }
            }

            if (!isSignedIn && !isLoading) {
                SignInSection(onGoogleSignIn = onGoogleSignIn)
            }
        }

        Spacer(modifier = Modifier.height(32.adp))
    }
}

@Composable
private fun DashboardSection(
    userProgress: UserProgressData?,
    onOpenProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.adp))
            .clickable(onClick = onOpenProgress),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = userProgress != null,
            enter = fadeIn(tween(300)) + slideInVertically { -it },
            exit = fadeOut(tween(200)) + slideOutVertically { -it }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.adp)
            ) {
                MiniProgressDashboard(
                    data = userProgress ?: UserProgressData(),
                    modifier = Modifier.widthIn(max = 360.adp)
                )
                Text(
                    text = "Tap to view details",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    isSignedIn: Boolean,
    userName: String?,
    isLoading: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.adp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSignedIn && !isLoading) {
            SignedInBadge(userName = userName)
        } else {
            Box(modifier = Modifier.weight(1f))
        }

        if (!isLoading) {
            CustomFilledIconButton(
                icon = Icons.Rounded.ArrowForwardIos,
                onClick = onBack,
                contentDescription = stringResource(R.string.action_close),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(0.5f),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                size = 44.adp
            )
        }
    }
}

@Composable
private fun SignedInBadge(userName: String?, modifier: Modifier = Modifier) {
    val semantic = MaterialTheme.lingoLens.semantic

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.adp))
            .background(semantic.success.copy(alpha = 0.1f))
            .border(1.adp, semantic.success.copy(alpha = 0.2f), RoundedCornerShape(12.adp))
            .padding(horizontal = 12.adp, vertical = 8.adp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.adp)
    ) {
        Box(
            modifier = Modifier
                .size(20.adp)
                .clip(CircleShape)
                .background(semantic.success),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_shield_check),
                contentDescription = null,
                modifier = Modifier.size(12.adp),
                tint = semantic.onSuccess
            )
        }

        Column {
            Text(
                text = "Signed in",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold, fontSize = 10.asp
                ),
                color = semantic.success
            )
            if (!userName.isNullOrBlank()) {
                Text(
                    text = userName.split(" ").firstOrNull() ?: userName,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.asp),
                    color = semantic.success.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun SignInSection(onGoogleSignIn: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.adp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.adp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            Text(
                text = "OR",
                modifier = Modifier.padding(horizontal = 16.adp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        }

        SaveProgressBanner()
        GoogleSignInButton(onClick = onGoogleSignIn)
    }
}

@Composable
private fun SaveProgressBanner(modifier: Modifier = Modifier) {
    val semantic = MaterialTheme.lingoLens.semantic

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.adp))
            .background(semantic.info.copy(alpha = 0.08f))
            .border(1.adp, semantic.info.copy(alpha = 0.15f), RoundedCornerShape(12.adp))
            .padding(12.adp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.adp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_shield_check),
            contentDescription = null,
            modifier = Modifier.size(18.adp),
            tint = semantic.info
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Sign in to save progress",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Sync across devices & keep your streak",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun TopicInput(
    topic: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = topic,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.adp, RoundedCornerShape(16.adp))
            .clip(RoundedCornerShape(16.adp)),
        placeholder = {
            Text(
                text = "Choose a topic (optional)",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.icon_search),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.adp)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(16.adp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun AppLogo() {
    Box(modifier = Modifier.size(130.adp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(100.adp)
                .graphicsLayer { rotationZ = 6f }
                .shadow(
                    20.adp, RoundedCornerShape(28.adp),
                    ambientColor = BrandColors.Indigo500.copy(0.25f),
                    spotColor = BrandColors.Purple600.copy(0.3f)
                )
                .background(
                    Brush.linearGradient(listOf(BrandColors.Indigo500, BrandColors.Purple600)),
                    RoundedCornerShape(28.adp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.icon_graduation_cap),
                contentDescription = null,
                modifier = Modifier.size(50.adp),
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier
                .size(40.adp)
                .align(Alignment.BottomEnd)
                .offset(x = 8.adp, y = 8.adp)
                .graphicsLayer { rotationZ = -15f }
                .shadow(10.adp, RoundedCornerShape(12.adp))
                .background(BrandColors.Emerald500, RoundedCornerShape(12.adp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aa",
                fontSize = 16.asp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun AppTitle() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.adp)
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append("Lingo") }
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) { append("Flow") }
            },
            fontSize = 44.asp,
            letterSpacing = (-1).asp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = buildAnnotatedString {
                append("Master ")
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                ) { append("English") }
                append(" with smart ")
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                ) { append("flashcards") }
            },
            fontSize = 15.asp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.asp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun WelcomeScreenPreview() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        WelcomeScreen(isSignedIn = false)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun WelcomeScreenSignedInPreview() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        WelcomeScreen(
            isSignedIn = true,
            userProgress = UserProgressData(
                totalWordsLearned = 156, masteredCount = 42, currentStreak = 7, todayXp = 85
            )
        )
    }
}