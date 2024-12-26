package com.venom.ui.components.items


import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.venom.data.model.OcrEntry
import com.venom.ui.components.bars.BookMarkActionButtons
import com.venom.ui.components.bars.HistoryItemHeader
import com.venom.utils.Extensions.toBitmap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun OcrHistoryItemView(
    entry: OcrEntry,
    onEntryRemove: (OcrEntry) -> Unit,
    onToggleBookmark: (OcrEntry) -> Unit,
    onShareClick: (OcrEntry) -> Unit,
    onCopyClick: (OcrEntry) -> Unit,
    onItemClick: (OcrEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showCopiedToast by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ElevatedCard(elevation = CardDefaults.cardElevation(
        defaultElevation = 2.dp, pressedElevation = 4.dp
    ), modifier = modifier
        .fillMaxWidth()
        .clickable {
            isExpanded = !isExpanded
            onItemClick(entry)
        }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize()
        ) {
            HistoryItemHeader(timestamp = entry.timestamp,
                isBookmarked = entry.isBookmarked,
                onToggleBookmark = { onToggleBookmark(entry) })

            OcrContent(
                text = entry.recognizedText, imageData = entry.imageData, isExpanded = isExpanded
            )

            BookMarkActionButtons(onShareClick = { onShareClick(entry) }, onCopyClick = {
                onCopyClick(entry)
                scope.launch {
                    showCopiedToast = true
                    delay(2000)
                    showCopiedToast = false
                }
            }, onDeleteClick = { onEntryRemove(entry) }, showCopiedToast = showCopiedToast
            )
        }
    }
}

@Composable
private fun OcrContent(
    text: String, imageData: ByteArray, isExpanded: Boolean, modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = if (isExpanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Image(
            bitmap = imageData.toBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isExpanded) 200.dp else 120.dp)
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )
    }
}

