package com.venom.stackcard.ui.components.mastery

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.domain.model.AppTheme
import com.venom.ui.components.other.BiDiFormatter
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.theme.PlaypenSans
import com.venom.ui.theme.lingoLens
import kotlin.random.Random

enum class StickyVariant { YELLOW, AMBER, ROSE, BLUE }

data class StickyConfig(
    val rotation: Float,
    val tapeOffset: Float,
    val variant: StickyVariant
)

@Composable
fun StickyNoteCard(
    mnemonicText: String,
    wordId: Any,
    modifier: Modifier = Modifier
) {
    var stickyConfig by remember(wordId) {
        mutableStateOf(
            StickyConfig(
                rotation = Random.nextFloat() * 6f - 3f,
                tapeOffset = Random.nextFloat() * 30f - 15f,
                variant = StickyVariant.entries.random()
            )
        )
    }

    val colorScheme = when (stickyConfig.variant) {
        StickyVariant.YELLOW -> MaterialTheme.lingoLens.sticky.yellow
        StickyVariant.AMBER -> MaterialTheme.lingoLens.sticky.amber
        StickyVariant.ROSE -> MaterialTheme.lingoLens.sticky.rose
        StickyVariant.BLUE -> MaterialTheme.lingoLens.sticky.blue
    }

    val animatedRotation by animateFloatAsState(
        targetValue = stickyConfig.rotation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .padding(top = 24.dp)
    ) {
        // Main Note Body (rotates on click)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .rotate(animatedRotation)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    stickyConfig = stickyConfig.copy(rotation = Random.nextFloat() * 6f - 3f)
                }
                .shadow(8.dp, RoundedCornerShape(10.dp), ambientColor = colorScheme.label.copy(0.2f))
                .background(colorScheme.bg, RoundedCornerShape(10.dp))
                .border(0.5.dp, colorScheme.border, RoundedCornerShape(10.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "MEMORY HOOK",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = colorScheme.label,
                    letterSpacing = 2.sp
                )

                Text(
                    text = remember(mnemonicText) { BiDiFormatter.format(mnemonicText) },
                    fontSize = 20.sp,
                    color = colorScheme.text,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp,
                    fontFamily = PlaypenSans,
                    style = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.Content)
                )
            }
        }

        Box(
            modifier = Modifier
                .offset(x = (stickyConfig.tapeOffset).dp, y = (-12).dp)
                .width(80.dp)
                .height(28.dp)
                .rotate(-2f)
                .blur(5.dp)
                .alpha(0.8f)
                .align(Alignment.TopCenter)
                .shadow(2.dp, RoundedCornerShape(2.dp), ambientColor = Color.Black.copy(0.1f))
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            colorScheme.tape.copy(0.85f),
                            colorScheme.tape.copy(0.75f),
                            colorScheme.tape.copy(0.85f)
                        )
                    ),
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
fun StickyNotePreview() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        StickyNoteCard(
            mnemonicText = "The word (كلمة) means 'word' in Arabic",
            wordId = 101,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F5F9)
@Composable
fun StickyNotePreviewLight() {
    LingoLensTheme(appTheme = AppTheme.LIGHT) {
        StickyNoteCard(
            mnemonicText = "The word (كلمة) means 'word' in Arabic",
            wordId = 101,
            modifier = Modifier.padding(24.dp)
        )
    }
}