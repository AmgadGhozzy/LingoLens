package com.venom.dialog.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dialog_messages")
data class DialogMessage(
    @PrimaryKey val id: String,
    val sourceText: String,
    val translatedText: String,
    val sourceLanguageCode: String,
    val sourceLanguageName: String,
    val targetLanguageCode: String,
    val targetLanguageName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSender: Boolean
)
