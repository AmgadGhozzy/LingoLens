package com.venom.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListStringConverter {
    private val gson = Gson()
    private val type = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun fromString(value: String?): List<String> {
        if (value == null) return emptyList()
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        if (list == null) return "[]"
        return gson.toJson(list, type)
    }
}
