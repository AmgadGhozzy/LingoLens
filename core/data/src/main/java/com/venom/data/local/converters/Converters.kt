package com.venom.data.local.converters

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.venom.domain.model.Definition
import com.venom.domain.model.DictionaryEntry
import com.venom.domain.model.DictionaryTerm
import com.venom.domain.model.Synset
import com.venom.domain.model.TranslationSentence

class Converters {
    private val moshi = Moshi.Builder().build()

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java)).toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java)).fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun fromTranslationSentenceList(value: List<TranslationSentence>): String {
        return moshi.adapter<List<TranslationSentence>>(Types.newParameterizedType(List::class.java, TranslationSentence::class.java)).toJson(value)
    }

    @TypeConverter
    fun toTranslationSentenceList(value: String): List<TranslationSentence> {
        return moshi.adapter<List<TranslationSentence>>(Types.newParameterizedType(List::class.java, TranslationSentence::class.java)).fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun fromDictionaryEntryList(value: List<DictionaryEntry>): String {
        return moshi.adapter<List<DictionaryEntry>>(Types.newParameterizedType(List::class.java, DictionaryEntry::class.java)).toJson(value)
    }

    @TypeConverter
    fun toDictionaryEntryList(value: String): List<DictionaryEntry> {
        return moshi.adapter<List<DictionaryEntry>>(Types.newParameterizedType(List::class.java, DictionaryEntry::class.java)).fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun fromSynsetList(value: List<Synset>): String {
        return moshi.adapter<List<Synset>>(Types.newParameterizedType(List::class.java, Synset::class.java)).toJson(value)
    }

    @TypeConverter
    fun toSynsetList(value: String): List<Synset> {
        return moshi.adapter<List<Synset>>(Types.newParameterizedType(List::class.java, Synset::class.java)).fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun fromDefinitionList(value: List<Definition>): String {
        return moshi.adapter<List<Definition>>(Types.newParameterizedType(List::class.java, Definition::class.java)).toJson(value)
    }

    @TypeConverter
    fun toDefinitionList(value: String): List<Definition> {
        return moshi.adapter<List<Definition>>(Types.newParameterizedType(List::class.java, Definition::class.java)).fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun fromDictionaryTermList(value: List<DictionaryTerm>): String {
        return moshi.adapter<List<DictionaryTerm>>(Types.newParameterizedType(List::class.java, DictionaryTerm::class.java)).toJson(value)
    }

    @TypeConverter
    fun toDictionaryTermList(value: String): List<DictionaryTerm> {
        return moshi.adapter<List<DictionaryTerm>>(Types.newParameterizedType(List::class.java, DictionaryTerm::class.java)).fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun fromStringListMap(value: Map<String, List<String>>): String {
        val mapType = Types.newParameterizedType(Map::class.java, String::class.java, Types.newParameterizedType(List::class.java, String::class.java))
        return moshi.adapter<Map<String, List<String>>>(mapType).toJson(value)
    }

    @TypeConverter
    fun toStringListMap(value: String): Map<String, List<String>> {
        val mapType = Types.newParameterizedType(Map::class.java, String::class.java, Types.newParameterizedType(List::class.java, String::class.java))
        return try {
            moshi.adapter<Map<String, List<String>>>(mapType).fromJson(value) ?: emptyMap()
        } catch(e: Exception) {
            emptyMap()
        }
    }
}
