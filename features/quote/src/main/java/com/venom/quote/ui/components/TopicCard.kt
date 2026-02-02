package com.venom.quote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.venom.domain.model.Tag
import com.venom.resources.R
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp

@Composable
fun TopicCard(
    tag: Tag,
    onClick: (String) -> Unit,
    onLongClick: ((String) -> Unit)? = null,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    SelectableItemCard(
        isSelected = isSelected,
        onClick = { onClick(tag.tagName) },
        onLongClick = onLongClick?.let { handler -> { handler(tag.tagName) } },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.adp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.adp)
                    .background(
                        colorScheme.primary.copy(0.2f),
                        RoundedCornerShape(12.adp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Tag,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(24.adp)
                )
            }

            Spacer(modifier = Modifier.height(12.adp))

            Text(
                text = stringResource(id = R.string.topic_tag_prefix, tag.tagName),
                color = colorScheme.onSurface,
                fontSize = 15.asp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.adp))

            Text(
                text = stringResource(id = R.string.topic_quotes_count, tag.count),
                color = colorScheme.onSurfaceVariant,
                fontSize = 13.asp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TopicsGrid(
    tags: List<Tag>,
    selectedTags: Set<String> = emptySet(),
    onTagClick: (String) -> Unit,
    onTagToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(top = 16.adp, bottom = 100.adp),
        horizontalArrangement = Arrangement.spacedBy(12.adp),
        verticalArrangement = Arrangement.spacedBy(12.adp),
        modifier = modifier.fillMaxSize()
    ) {
        items(tags) { tag ->
            TopicCard(
                tag = tag,
                onClick = onTagClick,
                onLongClick = onTagToggle,
                isSelected = selectedTags.contains(tag.tagName)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopicCardPreview() {
    Column(
        modifier = Modifier.padding(16.adp),
        verticalArrangement = Arrangement.spacedBy(16.adp)
    ) {
        TopicCard(
            tag = Tag(0, "Inspiration", 5),
            isSelected = false,
            onClick = {}
        )

        TopicCard(
            tag = Tag(1, "Motivation", 12),
            isSelected = true,
            onClick = {}
        )
    }
}