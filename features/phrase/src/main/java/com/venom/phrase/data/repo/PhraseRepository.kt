package com.venom.phrase.data.repo

import com.venom.phrase.data.local.dao.PhraseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhraseRepository(private val dao: PhraseDao) {
    suspend fun getAllCategories() = withContext(Dispatchers.IO) { dao.getAllCategories() }
    suspend fun getSectionsWithPhrases(categoryId: Int) = withContext(Dispatchers.IO) {
        dao.getSectionsWithPhrases(categoryId)
    }
}
