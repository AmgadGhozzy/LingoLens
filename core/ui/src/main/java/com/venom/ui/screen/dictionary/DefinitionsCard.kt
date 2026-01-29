package com.venom.ui.screen.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.domain.model.Definition
import com.venom.resources.R
import com.venom.ui.components.items.WordChip
import com.venom.ui.components.other.GlassThickness
import com.venom.ui.components.other.GradientGlassCard
import com.venom.ui.theme.BrandColors


private val DEFINITION_GRADIENT_COLORS = listOf(
    BrandColors.Blue500,
    BrandColors.Purple600,
    BrandColors.Cyan500
)

@Composable
fun DefinitionsCard(
    definitions: List<Definition>,
    onTextClick: ((String) -> Unit)? = null,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    isSpeaking: Boolean = false
) {
    var showAll by rememberSaveable { mutableStateOf(false) }

    GradientGlassCard(
        thickness = GlassThickness.UltraThin,
        gradientColors = DEFINITION_GRADIENT_COLORS,
        gradientAlpha = 0.1f,
        contentPadding = 16.dp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionHeader(title = stringResource(id = R.string.definitions))

            val visibleDefinitions = if (showAll) definitions else definitions.take(3)

            visibleDefinitions.forEach { definition ->
                key(definition.pos) {
                    DefinitionSection(
                        definition = definition,
                        onTextClick = onTextClick,
                        onSpeak = onSpeak,
                        onCopy = onCopy,
                        onShare = onShare,
                        isSpeaking = isSpeaking
                    )
                }
            }

            if (definitions.size > 3) {
                WordChip(
                    word = if (showAll) {
                        stringResource(id = R.string.show_less)
                    } else {
                        stringResource(id = R.string.show_more)
                    },
                    onClick = { showAll = !showAll }
                )
            }
        }
    }
}