package com.venom.stackcard.data.repo

import com.venom.stackcard.data.local.dao.WordDao
import com.venom.stackcard.data.model.WordEntity
import com.venom.stackcard.domain.model.WordLevels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WordRepository @Inject constructor(
    private val wordDao: WordDao
) {
    fun getAllWords(): Flow<List<WordEntity>> = wordDao.getAllWords()

    fun get10Words(): Flow<List<WordEntity>> = wordDao.get10Words()

    fun get10UnseenWords(): Flow<List<WordEntity>> = wordDao.get10UnseenWords()

    suspend fun getWordsFromLevel(
        level: WordLevels = WordLevels.Beginner,
        includeRemembered: Boolean = false,
        includeForgotten: Boolean = false,
        orderBy: String = "RANKING",
        pageSize: Int = 10
    ): List<WordEntity> = withContext(Dispatchers.IO) {
        wordDao.getWordsFromLevel(
            minRank = level.range.start,
            maxRank = level.range.end,
            includeRemembered = includeRemembered,
            includeForgotten = includeForgotten,
            orderBy = orderBy,
            pageSize = pageSize
        )
    }

    fun getBookmarkedWords(): Flow<List<WordEntity>> = wordDao.getBookmarkedWords()

    fun getRememberedWords(): Flow<List<WordEntity>> = wordDao.getRememberedWords()

    fun getForgotedWords(): Flow<List<WordEntity>> = wordDao.getForgotedWords()

    suspend fun toggleBookmark(wordEntity: WordEntity) = withContext(Dispatchers.IO) {
        wordDao.update(wordEntity.copy(isBookmarked = !wordEntity.isBookmarked))
    }

    suspend fun toggleRemember(wordEntity: WordEntity) = withContext(Dispatchers.IO) {
        wordDao.update(wordEntity.copy(isRemembered = !wordEntity.isRemembered))
    }

    suspend fun toggleForgot(wordEntity: WordEntity) = withContext(Dispatchers.IO) {
        wordDao.update(wordEntity.copy(isForgotten = !wordEntity.isForgotten))
    }
}
