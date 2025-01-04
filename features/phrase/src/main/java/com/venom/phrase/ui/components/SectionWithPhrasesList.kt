package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.venom.phrase.data.mapper.getTranslation
import com.venom.phrase.data.model.SectionWithPhrases


@Composable
fun SectionWithPhrasesList(
    sections: List<SectionWithPhrases>,
    sourceLang: String,
    targetLang: String,
    contentPadding: PaddingValues
) {
    LazyColumn(
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding(),
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        sections.forEach { sectionWithPhrases ->
            item(key = "header_${sectionWithPhrases.section.sectionId}") {
                SectionHeader(title = sectionWithPhrases.section.getTranslation(sourceLang))
            }
            items(
                items = sectionWithPhrases.phrases,
                key = { it.phraseId }
            ) { phrase ->
                PhraseExpandCard(
                    phrase = phrase,
                    sourceLang = sourceLang,
                    targetLang = targetLang
                )
            }
        }
    }
}