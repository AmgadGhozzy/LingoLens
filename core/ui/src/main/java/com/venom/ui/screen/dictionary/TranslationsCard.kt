package com.venom.ui.screen.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.domain.model.DictionaryEntry
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.other.GlassThickness
import com.venom.ui.components.other.GradientGlassCard
import com.venom.ui.theme.BrandColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TranslationsCard(
    translations: List<DictionaryEntry>,
    onWordClick: (String) -> Unit,
    onSpeak: (String) -> Unit,
    onExpand: (() -> Unit)? = null,
) {
    var showAll by remember { mutableStateOf(false) }

    GradientGlassCard(
        thickness = GlassThickness.UltraThin,
        gradientColors = listOf(
            BrandColors.Blue500,
            BrandColors.Purple600,
            BrandColors.Cyan500
        ),
        gradientAlpha = 0.1f,
        contentPadding = 12.dp
    ){
        Box(modifier = Modifier.padding(12.dp)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(title = stringResource(id = R.string.translations))

                val visibleTranslations = if (showAll) translations else translations.take(3)
                visibleTranslations.forEach { entry ->
                    TranslationEntryComponent(
                        entry = entry,
                        showAll = showAll,
                        onWordClick = onWordClick,
                        onSpeak = onSpeak,
                        toggleShowAll = { showAll = !showAll }
                    )
                }
            }

            onExpand?.let {
                CustomButton(
                    icon = R.drawable.icon_fullscreen,
                    onClick = it,
                    contentDescription = stringResource(R.string.action_expand),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp)
                )
            }
        }
    }
}