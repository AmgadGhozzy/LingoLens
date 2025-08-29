package com.venom.data.repo

import com.venom.data.local.Entity.WordEntity
import com.venom.data.local.dao.WordDao
import com.venom.domain.model.WordLevels
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
        pageSize: Int = 10
    ): List<WordEntity> = withContext(Dispatchers.IO) {
        wordDao.getRandomWordsFromLevel(
            minRank = level.range.start,
            maxRank = level.range.end,
            includeRemembered = includeRemembered,
            includeForgotten = includeForgotten,
            pageSize = pageSize
        )
    }

    // Add to WordRepository.kt
    suspend fun getLevelProgress(level: WordLevels): Float = withContext(Dispatchers.IO) {
        val totalWords = wordDao.getWordCountInLevel(level.range.start, level.range.end)
        val seenWords = wordDao.getSeenWordCountInLevel(level.range.start, level.range.end)

        if (totalWords == 0) 0f else seenWords.toFloat() / totalWords.toFloat()
    }

    suspend fun getLevelsProgress(): Map<String, Float> = withContext(Dispatchers.IO) {
        WordLevels.Companion.values().associate { level ->
            level.id to getLevelProgress(level)
        }
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