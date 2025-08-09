package com.venom.phrase.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
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
            PhrasebookTitle(
                totalPhrases = totalPhrases
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        scrollBehavior = scrollBehavior,
        actions = {
            LanguageBar(
                viewModel = viewModel,
                modifier = Modifier
                    .height(56.dp)
                    .width(256.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(28.dp)
                    )
            )
            CustomButton(
                icon = R.drawable.icon_bookmark_filled,
                contentDescription = stringResource(R.string.bookmarks_title),
                onClick = onBookmarkClick,
                iconSize = 40.dp,
                showBorder = false
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor =Color.Transparent
        )
    )
}
