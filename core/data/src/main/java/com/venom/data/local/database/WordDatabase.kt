package com.venom.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.venom.data.local.Entity.WordEntity
import com.venom.data.local.converters.ListStringConverter
import com.venom.data.local.dao.WordDao

@Database(entities = [WordEntity::class], version = 1, exportSchema = false)
@TypeConverters(ListStringConverter::class)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}