package com.venom.phrase.data.repo

import com.venom.phrase.data.local.dao.PhraseDao
import com.venom.phrase.data.model.Phrase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


class PhraseRepository(private val dao: PhraseDao) {
    suspend fun getAllCategories() = withContext(Dispatchers.IO) {
        dao.getAllCategories()
    }

    suspend fun getSectionsWithPhrases(categoryId: Int) = withContext(Dispatchers.IO) {
        dao.getSectionsWithPhrases(categoryId)
    }

    fun get10UnseenPhrases(): Flow<List<Phrase>> = dao.get10UnseenPhrases()

    fun getBookmarkedPhrases(): Flow<List<Phrase>> = dao.getBookmarkedPhrases()

    fun getRememberedPhrases(): Flow<List<Phrase>> = dao.getRememberedPhrases()

    fun getForgottenPhrases(): Flow<List<Phrase>> = dao.getForgotedPhrases()

    suspend fun toggleBookmark(phrase: Phrase) = withContext(Dispatchers.IO) {
        dao.update(phrase.copy(isBookmarked = !phrase.isBookmarked))
    }

    suspend fun toggleRemember(phrase: Phrase) = withContext(Dispatchers.IO) {
        dao.update(phrase.copy(isRemembered = !phrase.isRemembered))
    }

    suspend fun toggleForgot(phrase: Phrase) = withContext(Dispatchers.IO) {
        dao.update(phrase.copy(isForgotten = !phrase.isForgotten))
    }

    suspend fun update(phrase: Phrase) = withContext(Dispatchers.IO) {
        dao.update(phrase)
    }
}
