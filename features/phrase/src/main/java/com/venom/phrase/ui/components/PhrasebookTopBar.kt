package com.venom.phrase.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.bars.LanguageBar
import com.venom.ui.viewmodel.LangSelectorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhrasebookTopBar(
    viewModel: LangSelectorViewModel,
    totalPhrases: Int,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        scrollBehavior = scrollBehavior,
        title = { PhrasesAvailableRow(totalPhrases) },
        actions = {
            LanguageBar(
                viewModel = viewModel,
                modifier = Modifier
                    .width(224.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(
                        0.5.dp,
                        MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(28.dp)
                    )
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
    )
}

@Composable
private fun PhrasesAvailableRow(totalPhrases: Int) {
    Column {
        Text(
            text = stringResource(R.string.phrase_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Badge(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) { Text("$totalPhrases") }

            Text(
                text = stringResource(R.string.phrase),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}