package com.venom.ui.components.speech

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCancellationBehavior
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.venom.domain.model.SpeechState
import com.venom.resources.R
import com.venom.ui.components.common.adp

@Composable
fun AnimatedSpeechVisualizer(state: SpeechState) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.audio_wave4)
    )

    val animationProgress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = state is SpeechState.Listening,
        iterations = LottieConstants.IterateForever,
        cancellationBehavior = LottieCancellationBehavior.Immediately
    )

    Box(
        modifier = Modifier
            .size(160.adp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { animationProgress },
            modifier = Modifier.size(100.adp)
        )
    }
}