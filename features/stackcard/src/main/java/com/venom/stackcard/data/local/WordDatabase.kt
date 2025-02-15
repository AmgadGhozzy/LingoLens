package com.venom.stackcard.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.venom.data.local.typeconverter.ListStringConverter
import com.venom.stackcard.data.local.dao.WordDao
import com.venom.stackcard.data.model.WordEntity

@Database(entities = [WordEntity::class], version = 1, exportSchema = false)
@TypeConverters(ListStringConverter::class)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}
