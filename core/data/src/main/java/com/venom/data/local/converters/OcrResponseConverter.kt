package com.venom.data.local.converters

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.venom.data.remote.respnod.OcrResponse
import com.venom.data.remote.respnod.ParagraphBox

object OcrResponseConverter {

    private val moshi = Moshi.Builder().build()
    
    @TypeConverter
    fun fromOcrResponse(value: OcrResponse): String {
        return moshi.adapter(OcrResponse::class.java).toJson(value)
    }

    @TypeConverter
    fun toOcrResponse(value: String): OcrResponse? {
        return try {
            moshi.adapter(OcrResponse::class.java).fromJson(value)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun fromParagraphBoxList(value: List<ParagraphBox>): String {
        val type = Types.newParameterizedType(List::class.java, ParagraphBox::class.java)
        return moshi.adapter<List<ParagraphBox>>(type).toJson(value)
    }

    @TypeConverter
    fun toParagraphBoxList(value: String): List<ParagraphBox> {
        val type = Types.newParameterizedType(List::class.java, ParagraphBox::class.java)
        return try {
            moshi.adapter<List<ParagraphBox>>(type).fromJson(value) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
