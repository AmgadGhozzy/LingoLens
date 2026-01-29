package com.venom.stackcard.ui.components.mastery

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.domain.model.AppTheme
import com.venom.resources.R
import com.venom.ui.theme.BrandColors
import com.venom.ui.theme.LingoLensTheme

/**
 * Bottom action bar for word card interactions.
 */
@Composable
fun ActionBar(
    onFlip: () -> Unit,
    onPractice: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularButton(
            iconResId = R.drawable.ic_list_dashes,
            contentDescription = stringResource(R.string.mastery_info),
            onClick = onInfoClick
        )
        PracticeButton(
            onClick = onPractice,
            modifier = Modifier.weight(1f, fill = false)
        )
        CircularButton(
            iconResId = R.drawable.ic_arrows_clockwise,
            contentDescription = stringResource(R.string.mastery_flip),
            onClick = onFlip
        )
    }
}

@Composable
private fun CircularButton(
    iconResId: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "circular_button_scale"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(56.dp)
            .scale(scale)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(0.2f),
                spotColor = Color.Black.copy(0.2f)
            )
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, Color.White.copy(0.1f), CircleShape),
        interactionSource = interactionSource
    ) {
        Icon(
            painter = painterResource(iconResId),
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PracticeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f)

    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .scale(scale)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(0.3f),
                spotColor = MaterialTheme.colorScheme.primary.copy(0.3f)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp),
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            BrandColors.Blue600,
                            BrandColors.Blue700
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_pencil_simple),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Text(
                    text = stringResource(R.string.mastery_practice),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun ActionBarPreview() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ActionBar(
                onFlip = {},
                onPractice = {},
                onInfoClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F5F9)
@Composable
private fun ActionBarPreviewLight() {
    LingoLensTheme(appTheme = AppTheme.LIGHT) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ActionBar(
                onFlip = {},
                onPractice = {},
                onInfoClick = {}
            )
        }
    }
}