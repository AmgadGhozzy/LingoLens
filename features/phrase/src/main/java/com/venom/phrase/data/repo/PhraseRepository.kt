package com.venom.phrase.data.repo

import com.venom.phrase.data.local.dao.PhraseDao
import com.venom.phrase.data.model.PhraseEntity
import com.venom.phrase.data.model.SectionWithPhrases
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

    suspend fun getSectionsWithBookmarkedPhrases(): List<SectionWithPhrases> =
        withContext(Dispatchers.IO) {
            val sections = dao.getSectionsWithPhrases()
            sections.map { sectionWithPhrases ->
                sectionWithPhrases.copy(
                    phrases = sectionWithPhrases.phrases.filter { it.isBookmarked }
                )
            }
        }

    fun get10UnseenPhrases(): Flow<List<PhraseEntity>> = dao.get10UnseenPhrases()

    fun getBookmarkedPhrases(): Flow<List<PhraseEntity>> = dao.getBookmarkedPhrases()

    fun getRememberedPhrases(): Flow<List<PhraseEntity>> = dao.getRememberedPhrases()

    fun getForgottenPhrases(): Flow<List<PhraseEntity>> = dao.getForgotedPhrases()

    suspend fun toggleBookmark(phrase: PhraseEntity) = withContext(Dispatchers.IO) {
        dao.update(phrase.copy(isBookmarked = !phrase.isBookmarked))
    }

    suspend fun toggleRemember(phrase: PhraseEntity) = withContext(Dispatchers.IO) {
        dao.update(phrase.copy(isRemembered = !phrase.isRemembered))
    }

    suspend fun toggleForgot(phrase: PhraseEntity) = withContext(Dispatchers.IO) {
        dao.update(phrase.copy(isForgotten = !phrase.isForgotten))
    }

    suspend fun update(phrase: PhraseEntity) = withContext(Dispatchers.IO) {
        dao.update(phrase)
    }
}
