package com.venom.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.venom.data.remote.respnod.OcrResponse
import com.venom.data.remote.respnod.ParagraphBox

object OcrResponseConverter {

    @TypeConverter
    fun fromOcrResponse(value: OcrResponse): String {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toOcrResponse(value: String): OcrResponse {
        val gson = Gson()
        val type = object : TypeToken<OcrResponse>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromParagraphBoxList(value: List<ParagraphBox>): String {
        val gson = Gson()
        val type = object : TypeToken<List<ParagraphBox>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toParagraphBoxList(value: String): List<ParagraphBox> {
        val gson = Gson()
        val type = object : TypeToken<List<ParagraphBox>>() {}.type
        return gson.fromJson(value, type)
    }
}
