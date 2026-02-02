package com.venom.stackcard.ui.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
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
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp
import com.venom.ui.components.onboarding.GoogleSignInButton
import com.venom.ui.theme.BrandColors
import com.venom.ui.theme.LingoLensTheme

@Composable
fun WelcomeScreen(
    onStart: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onGoogleSignIn: () -> Unit = {},
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    var topic by remember { mutableStateOf("") }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (!isLoading) {
            CustomFilledIconButton(
                icon = Icons.Rounded.ArrowForwardIos,
                modifier = Modifier.align(Alignment.TopEnd).padding(24.adp),
                onClick = onBack,
                contentDescription = stringResource(R.string.action_close),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
                ),
                size = 46.adp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.adp, vertical = 48.adp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(56.adp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            AppLogo()
            AppTitle()

            Spacer(modifier = Modifier.weight(0.5f))

            Column(
                modifier = Modifier.widthIn(max = 360.adp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.adp)
            ) {
//                if (!isLoading) {
//                    TopicInput(topic, onValueChange = { topic = it })
//                }

                StartButton(onClick = { onStart(topic) }, isLoading = isLoading)

                if (!isLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.adp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        Text(
                            text = "OR",
                            modifier = Modifier.padding(horizontal = 16.adp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }

                    GoogleSignInButton(
                        onClick = onGoogleSignIn
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun AppLogo() {
    Box(modifier = Modifier.size(140.adp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(120.adp)
                .graphicsLayer { rotationZ = 8f }
                .shadow(
                    elevation = 24.adp,
                    shape = RoundedCornerShape(32.adp),
                    ambientColor = BrandColors.Indigo500.copy(0.3f),
                    spotColor = BrandColors.Purple600.copy(0.4f)
                )
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(BrandColors.Indigo500, BrandColors.Purple600)
                    ),
                    shape = RoundedCornerShape(32.adp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.icon_graduation_cap),
                contentDescription = null,
                modifier = Modifier.size(60.adp),
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier
                .size(48.adp)
                .align(Alignment.BottomEnd)
                .offset(x = 14.adp, y = 14.adp)
                .graphicsLayer { rotationZ = -12f }
                .shadow(14.adp, RoundedCornerShape(16.adp))
                .background(BrandColors.Emerald500, RoundedCornerShape(16.adp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aa",
                fontSize = 20.asp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun AppTitle() {
    Column(
        modifier = Modifier.widthIn(max = 360.adp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.adp)
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                    append("Lingo")
                }
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    append("Flow")
                }
            },
            fontSize = 48.asp,
            letterSpacing = (-1).asp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = buildAnnotatedString {
                append("Accelerate your ")
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("English fluency")
                }
                append(" with smart ")
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("flashcards")
                }
            },
            fontSize = 16.asp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 26.asp
        )
    }
}

@Composable
private fun TopicInput(
    topic: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = topic,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.adp, RoundedCornerShape(16.adp))
            .clip(RoundedCornerShape(16.adp)),
        placeholder = {
            Text(
                text = "Choose a topic...",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.icon_search),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.adp)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(16.adp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun StartButton(onClick: () -> Unit, isLoading: Boolean) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val offsetY by animateDpAsState(
        targetValue = if (isPressed) 8.adp else 0.adp,
        animationSpec = tween(100)
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.adp)
                .offset(y = 8.adp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            BrandColors.Indigo600.copy(alpha = 0.6f),
                            BrandColors.Purple600.copy(alpha = 0.6f)
                        )
                    ),
                    shape = RoundedCornerShape(16.adp)
                )
        )

        Button(
            onClick = onClick,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(56.adp).offset(y = offsetY),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.adp),
            shape = RoundedCornerShape(16.adp),
            interactionSource = interactionSource
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(BrandColors.Indigo600, BrandColors.Purple600)
                        ),
                        shape = RoundedCornerShape(16.adp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    LottieAnimation(
                        composition = rememberLottieComposition(
                            LottieCompositionSpec.RawRes(R.raw.dot_loading)
                        ).value,
                        iterations = LottieConstants.IterateForever,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth(0.4f).blur(0.5.adp),
                        speed = 0.8f
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.adp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.adp)
                                .background(Color.White.copy(0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.icon_play),
                                contentDescription = null,
                                modifier = Modifier.size(16.adp),
                                tint = Color.White
                            )
                        }

                        Text(
                            text = "Start Learning",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.asp,
                            color = Color.White,
                            letterSpacing = 0.3.asp
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun WelcomeScreenPreview() {
    LingoLensTheme(appTheme = AppTheme.LIGHT) {
        WelcomeScreen()
    }
}