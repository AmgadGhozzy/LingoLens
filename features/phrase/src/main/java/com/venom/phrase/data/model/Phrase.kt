package com.venom.phrase.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phrases")
data class Phrase(
    @PrimaryKey val phrase_id: Int,
    val section_id: Int,
    val priority: Int,
    @ColumnInfo(name = "en-US") val english: String?
)