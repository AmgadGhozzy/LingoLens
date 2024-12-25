package com.venom.data.local.typeconverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.venom.data.model.BoundingBox
import com.venom.data.model.OcrResponse

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
    fun fromBoundingBoxList(value: List<BoundingBox>): String {
        val gson = Gson()
        val type = object : TypeToken<List<BoundingBox>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toBoundingBoxList(value: String): List<BoundingBox> {
        val gson = Gson()
        val type = object : TypeToken<List<BoundingBox>>() {}.type
        return gson.fromJson(value, type)
    }
}
