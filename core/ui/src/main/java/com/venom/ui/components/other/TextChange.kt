package com.venom.ui.components.other

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.venom.ui.components.common.DynamicStyledText
import org.intellij.lang.annotations.Language

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TextChange(
    text: String,
    modifier: Modifier = Modifier,
    animationDuration: Int = 800,
    blurIntensity: Float = 25f,
    cutoff: Float = 0.2f,
    fontWeight: FontWeight = FontWeight.Bold,
    textAlign: TextAlign = TextAlign.Center
) {
    var isAnimating by remember { mutableStateOf(false) }
    val blurAnimation = remember { Animatable(0f) }
    val venomColor = MaterialTheme.colorScheme.onBackground

    LaunchedEffect(text) {
        if (isAnimating) return@LaunchedEffect

        isAnimating = true
        // Blur in
        blurAnimation.animateTo(
            targetValue = blurIntensity,
            animationSpec = tween(animationDuration / 2, easing = LinearEasing)
        )
        // Blur out
        blurAnimation.animateTo(
            targetValue = 0f,
            animationSpec = tween(animationDuration / 2, easing = LinearEasing)
        )
        isAnimating = false
    }

    Box(
        modifier = modifier
            .animateContentSize()
            .clipToBounds()
            .applyVenomEffect(venomColor, cutoff)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .applyBlurEffect(blurAnimation.value)
        ) {
            AnimatedContent(
                targetState = text,
                modifier = Modifier.fillMaxWidth(),
                transitionSpec = {
                    (fadeIn(tween(animationDuration, easing = LinearEasing)) +
                            expandVertically(
                                tween(animationDuration, easing = LinearEasing),
                                expandFrom = Alignment.CenterVertically
                            ))
                        .togetherWith(
                            fadeOut(tween(animationDuration, easing = LinearEasing)) +
                                    shrinkVertically(
                                        tween(animationDuration, easing = LinearEasing),
                                        shrinkTowards = Alignment.CenterVertically
                                    )
                        )
                }
            ) { currentText ->
                DynamicStyledText(
                    text = currentText,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = fontWeight,
                    textAlign = textAlign
                )
            }
        }
    }
}

@Language("AGSL")
private const val VENOM_SHADER_LIGHT = """
    uniform shader composable;
    
    uniform float cutoff;
    uniform float3 venomColor;
    
    half4 main(float2 fragCoord) {
        half4 color = composable.eval(fragCoord);
        color.a = step(cutoff, color.a);

        if (color.a > 0.0) {
            color.rgb = half3(venomColor[0], venomColor[1], venomColor[2]);
        }

        return color;
    }
"""

fun Modifier.applyVenomEffect(
    venomColor: Color,
    cutoff: Float
): Modifier {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val venomShader = RuntimeShader(VENOM_SHADER_LIGHT)

        graphicsLayer {
            venomShader.setFloatUniform("cutoff", cutoff)
            venomShader.setFloatUniform("venomColor", venomColor.red, venomColor.green, venomColor.blue)
            renderEffect = RenderEffect
                .createRuntimeShaderEffect(venomShader, "composable")
                .asComposeRenderEffect()
        }
    } else {
        this
    }
}

fun Modifier.applyBlurEffect(blurValue: Float): Modifier {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && blurValue > 0f) {
        graphicsLayer {
            renderEffect = try {
                RenderEffect
                    .createBlurEffect(
                        blurValue.coerceIn(0f, 100f),
                        blurValue.coerceIn(0f, 100f),
                        Shader.TileMode.DECAL
                    )
                    .asComposeRenderEffect()
            } catch (_: Exception) {
                null
            }
        }
    } else {
        this
    }
}
