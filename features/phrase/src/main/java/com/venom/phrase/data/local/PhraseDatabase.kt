package com.venom.phrase.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.venom.phrase.data.local.dao.PhraseDao
import com.venom.phrase.data.model.Category
import com.venom.phrase.data.model.Phrase
import com.venom.phrase.data.model.Section

@Database(entities = [Phrase::class, Category::class, Section::class], version = 1)
abstract class PhraseDatabase : RoomDatabase() {
    abstract fun phraseDao(): PhraseDao
}