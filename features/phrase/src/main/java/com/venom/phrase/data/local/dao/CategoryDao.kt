package com.venom.phrase.data.local.dao

import android.icu.util.ULocale.Category
import androidx.room.Dao
import androidx.room.Query

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY priority")
    fun getAllCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE category_id = :categoryId")
    fun getCategoryById(categoryId: Int): Category
}