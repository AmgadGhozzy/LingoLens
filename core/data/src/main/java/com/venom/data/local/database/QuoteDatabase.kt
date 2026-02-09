package com.venom.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.venom.data.local.converters.StringListConverter
import com.venom.data.local.dao.QuoteDao
import com.venom.data.local.entity.AuthorEntity
import com.venom.data.local.entity.QuoteEntity
import com.venom.data.local.entity.QuoteTagCrossRef
import com.venom.data.local.entity.TagEntity

@Database(
    entities = [
        QuoteEntity::class,
        AuthorEntity::class,
        TagEntity::class,
        QuoteTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class QuoteDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
}