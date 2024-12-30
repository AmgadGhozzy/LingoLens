package com.venom.phrase.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.venom.phrase.data.model.Phrase

@Dao
interface PhraseDao {
    @Query("SELECT * FROM phrases WHERE section_id = :sectionId ORDER BY priority")
    fun getPhrasesForSection(sectionId: Int): List<Phrase>
}
