package com.venom.phrase.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.venom.phrase.data.local.dao.PhraseDao
import com.venom.phrase.data.model.Category
import com.venom.phrase.data.model.PhraseEntity
import com.venom.phrase.data.model.Section

@Database(entities = [PhraseEntity::class, Category::class, Section::class], version = 1, exportSchema = false)
abstract class PhraseDatabase : RoomDatabase() {
    abstract fun phraseDao(): PhraseDao
}