package com.venom.phrase.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.common.adp
import com.venom.ui.screen.langselector.LangSelectorViewModel
import com.venom.ui.screen.langselector.LanguageBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhrasebookTopBar(
    viewModel: LangSelectorViewModel,
    totalPhrases: Int,
    onBookmarkClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            PhrasebookTitle(totalPhrases = totalPhrases)
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        scrollBehavior = scrollBehavior,
        actions = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.adp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.adp)
            ) {
                LanguageBar(
                    langSelectorViewModel = viewModel,
                    modifier = Modifier
                        .height(52.adp)
                        .defaultMinSize(minWidth = 150.adp)
                        .widthIn(max = 248.adp)
                        .clip(RoundedCornerShape(26.adp))
                        .border(
                            width = 0.5.adp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(0.5f),
                            shape = RoundedCornerShape(26.adp)
                        )
                )
                
                CustomButton(
                    icon = R.drawable.icon_bookmark_filled,
                    contentDescription = stringResource(R.string.bookmarks_title),
                    onClick = onBookmarkClick,
                    iconSize = 36.adp,
                    showBorder = false
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    )
}
