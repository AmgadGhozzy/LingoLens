package com.venom.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.venom.domain.model.Definition
import com.venom.domain.model.DictionaryEntry
import com.venom.domain.model.DictionaryTerm
import com.venom.domain.model.Synset
import com.venom.domain.model.TranslationSentence

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromTranslationSentenceList(value: List<TranslationSentence>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTranslationSentenceList(value: String): List<TranslationSentence> {
        val listType = object : TypeToken<List<TranslationSentence>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromDictionaryEntryList(value: List<DictionaryEntry>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDictionaryEntryList(value: String): List<DictionaryEntry> {
        val listType = object : TypeToken<List<DictionaryEntry>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromSynsetList(value: List<Synset>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toSynsetList(value: String): List<Synset> {
        val listType = object : TypeToken<List<Synset>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromDefinitionList(value: List<Definition>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDefinitionList(value: String): List<Definition> {
        val listType = object : TypeToken<List<Definition>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromDictionaryTermList(value: List<DictionaryTerm>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDictionaryTermList(value: String): List<DictionaryTerm> {
        val listType = object : TypeToken<List<DictionaryTerm>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromStringListMap(value: Map<String, List<String>>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringListMap(value: String): Map<String, List<String>> {
        val mapType = object : TypeToken<Map<String, List<String>>>() {}.type
        return gson.fromJson(value, mapType) ?: emptyMap()
    }
}
