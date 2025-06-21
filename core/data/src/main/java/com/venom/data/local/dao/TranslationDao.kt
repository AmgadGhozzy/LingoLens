package com.venom.data.local.dao

import androidx.room.*
import com.venom.data.model.TranslationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationDao {

    @Query("""
        SELECT * FROM translations 
        WHERE sourceText = :sourceText 
        AND sourceLang = :sourceLang 
        AND targetLang = :targetLang 
        AND providerId = :providerId
        LIMIT 1
    """)
    suspend fun getCachedTranslation(
        sourceText: String,
        sourceLang: String,
        targetLang: String,
        providerId: String
    ): TranslationEntity?

    @Query("SELECT * FROM translations WHERE id = :id LIMIT 1")
    suspend fun getTranslationById(id: Long): TranslationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(translation: TranslationEntity): Long

    @Update
    suspend fun update(translation: TranslationEntity)

    @Delete
    suspend fun delete(translation: TranslationEntity)

    @Query("SELECT * FROM translations ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<TranslationEntity>>

    @Query("SELECT * FROM translations WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedEntries(): Flow<List<TranslationEntity>>

    @Query("SELECT * FROM translations WHERE providerId = :providerId ORDER BY timestamp DESC")
    fun getEntriesByProvider(providerId: String): Flow<List<TranslationEntity>>

    @Query("UPDATE translations SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: Long, isBookmarked: Boolean)

    @Query("UPDATE translations SET isBookmarked = 0")
    suspend fun clearBookmarks()

    @Query("DELETE FROM translations WHERE isBookmarked = 0")
    suspend fun deleteNonBookmarkedEntries()

    @Query("DELETE FROM translations")
    suspend fun clearAll()

    @Query("""
        SELECT * FROM translations 
        WHERE sourceText = :sourceText 
        AND sourceLang = :sourceLang 
        AND targetLang = :targetLang
        LIMIT 1
    """)
    suspend fun getTranslationEntity(
        sourceText: String,
        sourceLang: String,
        targetLang: String
    ): TranslationEntity?
}