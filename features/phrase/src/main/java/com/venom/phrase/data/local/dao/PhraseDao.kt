package com.venom.phrase.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.venom.phrase.data.model.Category
import com.venom.phrase.data.model.PhraseEntity
import com.venom.phrase.data.model.SectionWithPhrases
import kotlinx.coroutines.flow.Flow

@Dao
interface PhraseDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): List<Category>

    @Transaction
    @Query("SELECT * FROM SECTIONS WHERE categoryId = :categoryId")
    fun getSectionsWithPhrases(categoryId: Int): List<SectionWithPhrases>

    @Transaction
    @Query("""
        SELECT * FROM sections 
        WHERE sectionId IN (
            SELECT DISTINCT sectionId 
            FROM phrases 
            WHERE isBookmarked = 1
        )
    """)
    fun getSectionsWithPhrases(): List<SectionWithPhrases>

    @Query("SELECT * FROM phrases WHERE isRemembered = 0 AND isForgotten = 0 ORDER BY sectionId LIMIT 10")
    fun get10UnseenPhrases(): Flow<List<PhraseEntity>>

    @Query("SELECT * FROM phrases WHERE isBookmarked = 1")
    fun getBookmarkedPhrases(): Flow<List<PhraseEntity>>

    @Query("SELECT * FROM phrases WHERE isRemembered = 1")
    fun getRememberedPhrases(): Flow<List<PhraseEntity>>

    @Query("SELECT * FROM phrases WHERE isForgotten = 1")
    fun getForgotedPhrases(): Flow<List<PhraseEntity>>

    @Update
    suspend fun update(phrase: PhraseEntity)
}