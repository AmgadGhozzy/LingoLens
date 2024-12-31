package com.venom.phrase.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.venom.phrase.data.model.Phrase
import kotlinx.coroutines.flow.Flow

@Dao
interface PhraseDao {
    @Query("SELECT * FROM phrases WHERE section_id = :sectionId ORDER BY phrase_id")
    fun getPhrasesForSection(sectionId: Int): Flow<List<Phrase>>
}
