package com.venom.phrase.data.repo

import com.venom.phrase.data.local.dao.CategoryDao
import com.venom.phrase.data.local.dao.PhraseDao
import com.venom.phrase.data.local.dao.SectionDao
import com.venom.phrase.data.model.Category
import com.venom.phrase.data.model.Phrase
import com.venom.phrase.data.model.Section
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhraseRepository @Inject constructor(
    private val phraseDao: PhraseDao,
    private val categoryDao: CategoryDao,
    private val sectionDao: SectionDao
) {
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    fun getSectionsForCategory(categoryId: Int): Flow<List<Section>> =
        sectionDao.getSectionsForCategory(categoryId)

    fun getPhrasesForSection(sectionId: Int): Flow<List<Phrase>> =
        phraseDao.getPhrasesForSection(sectionId)
}