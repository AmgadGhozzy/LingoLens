package com.venom.phrase.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import com.venom.phrase.data.mapper.getTranslation
import com.venom.phrase.data.model.PhraseEntity
import com.venom.ui.components.common.DynamicStyledText
import com.venom.ui.components.common.adp
import com.venom.ui.theme.BrandColors

@Composable
fun ExpandedContent(
    visible: Boolean,
    phrase: PhraseEntity,
    targetLang: String,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 250)
        ) + expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 150)
        ) + shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(durationMillis = 200)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            GradientDivider(
                modifier = Modifier.padding(vertical = 16.adp)
            )

            // Translation
            DynamicStyledText(
                text = phrase.getTranslation(targetLang), modifier = Modifier.fillMaxWidth()
            )

            ActionButtons(
                onCopyClick = onCopyClick,
                onShareClick = onShareClick,
            )
        }
    }
}

@Composable
private fun GradientDivider(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(2.adp)
            .clip(RoundedCornerShape(1.adp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        BrandColors.Transparent,
                        BrandColors.Indigo600.copy(alpha = 0.3f),
                        BrandColors.Purple600.copy(alpha = 0.3f),
                        BrandColors.Indigo600.copy(alpha = 0.3f),
                        BrandColors.Transparent
                    )
                )
            )
    )
}