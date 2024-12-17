package com.venom.lingopro.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.venom.lingopro.data.local.dao.TranslationDao
import com.venom.lingopro.data.local.typeconverter.ListStringConverter
import com.venom.lingopro.data.model.TranslationEntry

@Database(entities = [TranslationEntry::class], version = 1, exportSchema = false)

@TypeConverters(ListStringConverter::class)
abstract class TranslationDatabase : RoomDatabase() {
    abstract fun translationDao(): TranslationDao
}