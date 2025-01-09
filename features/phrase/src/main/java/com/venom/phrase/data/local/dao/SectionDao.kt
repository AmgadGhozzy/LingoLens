package com.venom.phrase.data.local.dao

import androidx.room.Query
import com.venom.phrase.data.model.Section
import kotlinx.coroutines.flow.Flow

interface SectionDao {
    @Query("SELECT * FROM sections WHERE categoryId = :categoryId ORDER BY sectionId")
    fun getSectionsForCategory(categoryId: Int): Flow<List<Section>>
}