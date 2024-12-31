package com.venom.phrase.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.venom.phrase.data.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY category_id")
    fun getAllCategories(): Flow<List<Category>>
}