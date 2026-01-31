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

private data class StickyConfig(
    val rotation: Float,
    val tapeOffset: Float,
    val variant: StickyVariant
)

/**
 * Sticky Note Card for Memory Hooks - REALISTIC GRADIENT VERSION
 *
 * Features:
 * - Realistic paper texture with multi-stop gradients
 * - Subtle edge darkening for 3D depth effect
 * - Radial gradient overlay for paper fiber texture
 * - Random color selection per wordId
 * - Handwritten font (PlaypenSans)
 * - Semi-transparent frosted tape
 * - Click to rotate
 *
 * @param mnemonicText The memory hook text to display
 * @param wordId Unique identifier for randomization stability
 * @param modifier Modifier for styling
 */
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

    // Get color scheme for the selected variant
    val colorScheme = when (stickyConfig.variant) {
        StickyVariant.YELLOW -> MaterialTheme.lingoLens.sticky.yellow
        StickyVariant.AMBER -> MaterialTheme.lingoLens.sticky.amber
        StickyVariant.ROSE -> MaterialTheme.lingoLens.sticky.rose
        StickyVariant.BLUE -> MaterialTheme.lingoLens.sticky.blue
    }

    // Animate rotation on click
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
        // Main Note Body
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .rotate(animatedRotation)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    // Rotate to new random angle on click
                    stickyConfig = stickyConfig.copy(rotation = Random.nextFloat() * 6f - 3f)
                }
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(10.dp),
                    ambientColor = colorScheme.label.copy(0.2f)
                )
                // REALISTIC PAPER GRADIENT
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorScheme.bg.copy(1f),
                            colorScheme.bg.copy(0.98f),
                            colorScheme.bg.copy(0.96f),
                            colorScheme.bg.copy(0.94f),
                            colorScheme.bg.copy(0.92f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    width = 0.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorScheme.border.copy(0.6f),
                            colorScheme.border.copy(0.8f),
                            colorScheme.border.copy(1f)
                        )
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "MEMORY HOOK" label
                Text(
                    text = "MEMORY HOOK",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = colorScheme.label,
                    letterSpacing = 2.sp
                )

                // Mnemonic text with handwritten font
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

        // FROSTED TAPE with realistic translucency
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
                        colors = listOf(
                            colorScheme.tape.copy(0.9f),
                            colorScheme.tape.copy(0.7f),
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
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StickyNoteCard(
                mnemonicText = "The word (كلمة) means 'word' in Arabic - remember it by thinking of 'calm' words",
                wordId = 101
            )
            StickyNoteCard(
                mnemonicText = "Journey (رحلة) - think of traveling on a winding path through desert sands",
                wordId = 102
            )
            StickyNoteCard(
                mnemonicText = "Curious means wanting to know or learn something new",
                wordId = 103
            )
            StickyNoteCard(
                mnemonicText = "Beautiful (جميل) - sounds like 'jameel' which means pretty!",
                wordId = 104
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F5F9, name = "Light Theme - Paper Texture")
@Composable
fun StickyNotePreviewLight() {
    LingoLensTheme(appTheme = AppTheme.LIGHT) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StickyNoteCard(
                mnemonicText = "The word (كلمة) means 'word' in Arabic",
                wordId = 201
            )
            StickyNoteCard(
                mnemonicText = "Journey (رحلة) - traveling adventure",
                wordId = 202
            )
            StickyNoteCard(
                mnemonicText = "Curious = eager to learn",
                wordId = 203
            )
            StickyNoteCard(
                mnemonicText = "Beautiful (جميل) - pretty!",
                wordId = 204
            )
        }
    }
}