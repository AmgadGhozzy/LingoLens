package com.venom.phrase.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.venom.phrase.data.mapper.getTranslation
import com.venom.phrase.data.model.Category
import com.venom.phrase.ui.viewmodel.PhraseUiState
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.common.DynamicStyledText
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp
import com.venom.ui.components.other.GlassCard
import com.venom.ui.components.other.GlassThickness

@Composable
fun CategoryItemCard(
    state: PhraseUiState,
    category: Category,
    onClick: () -> Unit
) {
    GlassCard(
        onClick = onClick,
        thickness = GlassThickness.Regular,
        showBorder = false,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Category card: ${category.englishEn}" }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Row(
            modifier = Modifier
                .padding(16.adp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.adp)
        ) {
            // Gradient accent bar
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.adp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                        )
                    )
                    .padding(horizontal = 3.adp, vertical = 24.adp)
            )

            // Category icon
            CategoryIcon(category.icon)

            // Category info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.adp)
            ) {
                DynamicStyledText(
                    text = category.getTranslation(state.sourceLang.code),
                    minFontSize = 18,
                    maxFontSize = 24,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.adp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${category.phraseCount}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 14.asp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.phrases),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.asp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Navigate button
            CustomFilledIconButton(
                icon = Icons.Rounded.ChevronRight,
                contentDescription = "View ${category.getTranslation(state.targetLang.code)} phrases",
                onClick = onClick,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(0.09f),
                    contentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
