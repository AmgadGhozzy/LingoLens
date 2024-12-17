package com.venom.lingopro.data.local.typeconverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ListStringConverter {

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toList(value: String?): List<String>? {
        val listType = object : TypeToken<List<String>?>() {}.type
        return Gson().fromJson(value, listType)
    }
}