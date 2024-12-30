package com.venom.phrase.data.repo

import com.venom.phrase.data.local.dao.CategoryDao
import com.venom.phrase.data.local.dao.PhraseDao
import com.venom.phrase.data.model.Category
import com.venom.phrase.data.model.Phrase
import javax.inject.Inject

class PhraseRepository @Inject constructor(
    private val phraseDao: PhraseDao, private val categoryDao: CategoryDao
) {
    fun getPhrasesForSection(sectionId: Int): List<Phrase> =
        phraseDao.getPhrasesForSection(sectionId)

    fun getAllCategories(): List<Category> = categoryDao.getAllCategories()
    fun getCategoryById(categoryId: Int): Category = categoryDao.getCategoryById(categoryId)
}