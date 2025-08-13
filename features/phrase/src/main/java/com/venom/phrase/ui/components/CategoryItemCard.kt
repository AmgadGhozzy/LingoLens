package com.venom.phrase.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.venom.phrase.data.mapper.getTranslation
import com.venom.phrase.data.model.Category
import com.venom.phrase.ui.viewmodel.PhraseUiState
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.common.DynamicStyledText
import com.venom.ui.components.other.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryItemCard(
    state: PhraseUiState, category: Category, onClick: () -> Unit
) {
    GlassCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Category card: ${category.englishEn}" }
            .animateContentSize(spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CategoryIcon(category.icon)
            Column(modifier = Modifier.weight(1f)) {
                DynamicStyledText(
                    category.getTranslation(state.sourceLang.code), modifier = Modifier
                        .fillMaxWidth()
                )

                Text(
                    text = "${category.phraseCount} ${stringResource(R.string.phrases)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
