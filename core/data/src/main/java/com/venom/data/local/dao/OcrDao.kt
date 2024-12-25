package com.venom.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.venom.data.model.OcrEntry
import kotlinx.coroutines.flow.Flow
@Dao
interface OcrDao {
    @Query("SELECT * FROM ocr_history ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<OcrEntry>>

    @Transaction
    @Query("SELECT * FROM ocr_history WHERE isBookmarked = 1")
    fun getBookmarkedEntries(): Flow<List<OcrEntry>>

    @Insert
    suspend fun insert(entry: OcrEntry): Long

    @Update
    suspend fun update(entry: OcrEntry)

    @Delete
    suspend fun delete(entry: OcrEntry)

    @Query("DELETE FROM ocr_history WHERE isBookmarked = 1")
    suspend fun clearBookmarks()

    @Query("DELETE FROM ocr_history WHERE isBookmarked = 0")
    suspend fun deleteNonBookmarkedEntries()
}