package com.venom.data.local

import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.venom.data.local.dao.TranslationDao
import com.venom.data.local.typeconverter.ListStringConverter
import com.venom.data.model.TranslationEntry

@androidx.room.Database(entities = [TranslationEntry::class], version = 1, exportSchema = false)
@TypeConverters(ListStringConverter::class)
abstract class TranslationDatabase : RoomDatabase() {
    abstract fun translationDao(): TranslationDao
}