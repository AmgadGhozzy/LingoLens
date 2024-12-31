package com.venom.phrase.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.venom.phrase.data.local.dao.CategoryDao
import com.venom.phrase.data.local.dao.PhraseDao
import com.venom.phrase.data.model.Category
import com.venom.phrase.data.model.Phrase
import com.venom.phrase.data.model.Section

@Database(entities = [Phrase::class, Category::class, Section::class], version = 1, exportSchema = false)
abstract class PhraseDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun sectionDao(): CategoryDao
    abstract fun phraseDao(): PhraseDao

}