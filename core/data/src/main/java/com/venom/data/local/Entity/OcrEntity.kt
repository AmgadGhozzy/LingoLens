package com.venom.data.local.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.venom.data.local.converters.OcrResponseConverter
import com.venom.data.remote.respnod.OcrResponse
import com.venom.data.remote.respnod.ParagraphBox
import com.venom.domain.model.IHistoryEntry

@Entity(tableName = "ocr_history")
data class OcrEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recognizedText: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val imageData: ByteArray,
    @TypeConverters(OcrResponseConverter::class) val apiResponse: OcrResponse? = null,
    @TypeConverters(OcrResponseConverter::class) val boundingBoxes: List<ParagraphBox> = emptyList(),
    override val timestamp: Long = System.currentTimeMillis(),
    override val isBookmarked: Boolean = false,
) : IHistoryEntry
