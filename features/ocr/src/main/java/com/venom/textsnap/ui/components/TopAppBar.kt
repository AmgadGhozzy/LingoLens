package com.venom.textsnap.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrTopAppBar(
    onNavigateBack: () -> Unit, onSettings: () -> Unit, modifier: Modifier = Modifier
) {
    TopAppBar(title = {
        Text(
            text = "TextSnap",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }, navigationIcon = {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.Rounded.ArrowBackIosNew,
                contentDescription = "Navigate back"
            )
        }
    }, actions = {
        IconButton(onClick = onSettings) {
            Icon(
                imageVector = Icons.Rounded.Settings, contentDescription = "Settings"
            )
        }
    }, modifier = modifier
        .fillMaxWidth()
        .shadow(elevation = 12.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun OcrTopAppBarPreview() {
    MaterialTheme {
        OcrTopAppBar(onNavigateBack = {}, onSettings = {})
    }
}
