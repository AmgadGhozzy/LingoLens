package com.venom.data.local.typeconverter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ListStringConverter {

    @androidx.room.TypeConverter
    fun fromList(list: List<String>?): String? {
        return Gson().toJson(list)
    }

    @androidx.room.TypeConverter
    fun toList(value: String?): List<String>? {
        val listType = object : TypeToken<List<String>?>() {}.type
        return Gson().fromJson(value, listType)
    }
}