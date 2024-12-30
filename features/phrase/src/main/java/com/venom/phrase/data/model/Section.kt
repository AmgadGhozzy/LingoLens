package com.venom.phrase.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sections")
data class Section(
    @PrimaryKey val section_id: Int,
    val category_id: Int,
    val priority: Int,
    @ColumnInfo(name = "en-US") val english: String?
)