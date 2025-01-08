package com.venom.wordcard.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.venom.data.local.typeconverter.ListStringConverter
import com.venom.wordcard.data.local.dao.WordDao
import com.venom.wordcard.data.model.WordEntity

@Database(entities = [WordEntity::class], version = 1)
@TypeConverters(ListStringConverter::class)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}
