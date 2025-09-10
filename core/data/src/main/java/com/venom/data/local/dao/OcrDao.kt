package com.venom.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.venom.data.local.Entity.OcrEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface OcrDao {
    @Query("SELECT * FROM ocr_history ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<OcrEntity>>

    @Transaction
    @Query("SELECT * FROM ocr_history WHERE isBookmarked = 1")
    fun getBookmarkedEntries(): Flow<List<OcrEntity>>

    @Insert
    suspend fun insert(entry: OcrEntity): Long

    @Update
    suspend fun update(entry: OcrEntity)

    @Delete
    suspend fun delete(entry: OcrEntity)

    @Query("DELETE FROM ocr_history WHERE isBookmarked = 1")
    suspend fun clearBookmarks()

    @Query("DELETE FROM ocr_history WHERE isBookmarked = 0")
    suspend fun deleteNonBookmarkedEntries()
}