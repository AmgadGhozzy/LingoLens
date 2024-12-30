package com.venom.phrase.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val category_id: Int,
    val featured: Int,
    val priority: Int,
    val icon: String,
    @ColumnInfo(name = "en-US") val english: String?
)