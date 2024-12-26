package com.venom.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.venom.data.local.typeconverter.OcrResponseConverter

@Entity(tableName = "ocr_history")
data class OcrEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recognizedText: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val imageData: ByteArray,
    val isBookmarked: Boolean = false,

    val timestamp: Long = System.currentTimeMillis(),

    @TypeConverters(OcrResponseConverter::class) val apiResponse: OcrResponse? = null,
    @TypeConverters(OcrResponseConverter::class) val boundingBoxes: List<ParagraphBox> = emptyList(),
)
