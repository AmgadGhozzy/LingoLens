package com.venom.settings.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelector(
    selectedLanguage: String, onLanguageSelected: (String) -> Unit, modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val languages = remember {
        listOf(
            "Arabic" to "🇸🇦",
            "English" to "🇺🇸",
            "Spanish" to "🇪🇸",
            "French" to "🇫🇷",
            "German" to "🇩🇪",
            "Chinese" to "🇨🇳"
        )
    }

    SettingsItem(title = "Language",
        subtitle = selectedLanguage,
        icon = Icons.Default.Language,
        modifier = modifier,
        onClick = { showDialog = true },
        trailing = {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        })

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Select Language") },
            text = {
                LazyColumn {
                    items(languages.size) { index ->
                        val (language, flag) = languages[index]
                        ListItem(headlineContent = { Text("$flag  $language") }, trailingContent = {
                            if (language == selectedLanguage) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }, modifier = Modifier.clickable {
                            onLanguageSelected(language)
                            showDialog = false
                        })
                    }
                }
            },
            confirmButton = {}
        )
    }
}
