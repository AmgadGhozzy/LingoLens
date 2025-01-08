package com.venom.wordcard.data.repo

import com.venom.wordcard.data.local.dao.WordDao
import com.venom.wordcard.data.model.WordEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WordRepository @Inject constructor(
    private val wordDao: WordDao
) {
    fun getAllWords(): Flow<List<WordEntity>> = wordDao.getAllWords()

    fun get10Words(): Flow<List<WordEntity>> = wordDao.get10Words()

    fun get10UnseenWords(): Flow<List<WordEntity>> = wordDao.get10UnseenWords()

    fun getBookmarkedWords(): Flow<List<WordEntity>> = wordDao.getBookmarkedWords()

    fun getRememberedWords(): Flow<List<WordEntity>> = wordDao.getRememberedWords()

    fun getForgotedWords(): Flow<List<WordEntity>> = wordDao.getForgotedWords()

    suspend fun toggleBookmark(wordEntity: WordEntity) {
        wordDao.update(wordEntity.copy(isBookmarked = !wordEntity.isBookmarked))
    }

    suspend fun toggleRemember(wordEntity: WordEntity) {
        wordDao.update(wordEntity.copy(isRemembered = !wordEntity.isRemembered))
    }

    suspend fun toggleForgot(wordEntity: WordEntity) {
        wordDao.update(wordEntity.copy(isForgotten = !wordEntity.isForgotten))
    }
}
