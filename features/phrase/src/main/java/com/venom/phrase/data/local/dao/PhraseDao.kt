package com.venom.phrase.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.venom.phrase.data.model.Category
import com.venom.phrase.data.model.SectionWithPhrases

@Dao
interface PhraseDao {

    @Query("SELECT * FROM categories")
    fun getAllCategories(): List<Category>

    @Transaction
    @Query("SELECT * FROM SECTIONS WHERE categoryId = :categoryId")
    fun getSectionsWithPhrases(categoryId: Int): List<SectionWithPhrases>
}
