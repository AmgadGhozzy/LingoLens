package com.venom.lingopro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.venom.lingopro.data.model.TranslationEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationDao {
    @Query("SELECT * FROM translation_history ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<TranslationEntry>>

    @Query("SELECT * FROM translation_history WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedEntries(): Flow<List<TranslationEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: TranslationEntry)

    @Update
    suspend fun update(entry: TranslationEntry)

    @Delete
    suspend fun delete(entry: TranslationEntry)

    @Query("DELETE FROM translation_history WHERE isBookmarked = 1")
    suspend fun clearBookmarks()

    @Query("DELETE FROM translation_history WHERE isBookmarked = 0")
    suspend fun deleteNonBookmarkedEntries()
}