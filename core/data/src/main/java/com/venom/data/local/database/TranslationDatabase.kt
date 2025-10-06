package com.venom.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.venom.data.local.converters.StringListConverter
import com.venom.data.local.dao.TranslationDao
import com.venom.data.local.entity.TranslationEntity

@Database(entities = [TranslationEntity::class], version = 1, exportSchema = false)
@TypeConverters(StringListConverter::class)
abstract class TranslationDatabase : RoomDatabase() {
    abstract fun translationDao(): TranslationDao
}