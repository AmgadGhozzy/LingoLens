package com.venom.phrase.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.venom.resources.R
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp

@Composable
fun PhrasebookTitle(
    totalPhrases: Int
) {
    Column(
        modifier = Modifier.padding(end = 16.adp),
        verticalArrangement = Arrangement.spacedBy(4.adp)
    ) {
        Text(
            text = stringResource(R.string.phrase_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.asp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.adp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Static badge with phrase count
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.adp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 8.adp, vertical = 4.adp)
            ) {
                Text(
                    text = "$totalPhrases",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.asp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }

            Text(
                text = stringResource(R.string.phrases),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.asp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
