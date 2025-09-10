package com.venom.data.repo

import com.venom.data.local.dao.WordDao
import com.venom.data.mapper.WordMapper
import com.venom.domain.model.Word
import com.venom.domain.model.WordLevels
import com.venom.domain.repo.IWordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao
) : IWordRepository {

    override fun getAllWords(): Flow<List<Word>> =
        wordDao.getAllWords().map { entities -> WordMapper.toDomainList(entities) }

    override fun get10Words(): Flow<List<Word>> =
        wordDao.get10Words().map { entities -> WordMapper.toDomainList(entities) }

    override fun get10UnseenWords(): Flow<List<Word>> =
        wordDao.get10UnseenWords().map { entities -> WordMapper.toDomainList(entities) }

    override suspend fun getWordsFromLevel(
        level: WordLevels,
        includeRemembered: Boolean,
        includeForgotten: Boolean,
        pageSize: Int
    ): List<Word> = withContext(Dispatchers.IO) {
        val entities = wordDao.getRandomWordsFromLevel(
            minRank = level.range.start,
            maxRank = level.range.end,
            includeRemembered = includeRemembered,
            includeForgotten = includeForgotten,
            pageSize = pageSize
        )
        WordMapper.toDomainList(entities)
    }

    override suspend fun getLevelProgress(level: WordLevels): Float = withContext(Dispatchers.IO) {
        val totalWords = wordDao.getWordCountInLevel(level.range.start, level.range.end)
        val seenWords = wordDao.getSeenWordCountInLevel(level.range.start, level.range.end)
        if (totalWords == 0) 0f else seenWords.toFloat() / totalWords.toFloat()
    }

    override suspend fun getLevelsProgress(): Map<String, Float> = withContext(Dispatchers.IO) {
        WordLevels.values().associate { level ->
            level.id to getLevelProgress(level)
        }
    }

    override fun getBookmarkedWords(): Flow<List<Word>> =
        wordDao.getBookmarkedWords().map { entities -> WordMapper.toDomainList(entities) }

    override fun getRememberedWords(): Flow<List<Word>> =
        wordDao.getRememberedWords().map { entities -> WordMapper.toDomainList(entities) }

    override fun getForgotedWords(): Flow<List<Word>> =
        wordDao.getForgotedWords().map { entities -> WordMapper.toDomainList(entities) }

    override suspend fun toggleBookmark(word: Word) = withContext(Dispatchers.IO) {
        val entity = WordMapper.toData(word)
        wordDao.update(entity.copy(isBookmarked = !word.isBookmarked))
    }

    override suspend fun toggleRemember(word: Word) = withContext(Dispatchers.IO) {
        val entity = WordMapper.toData(word)
        wordDao.update(entity.copy(isRemembered = !word.isRemembered))
    }

    override suspend fun toggleForgot(word: Word) = withContext(Dispatchers.IO) {
        val entity = WordMapper.toData(word)
        wordDao.update(entity.copy(isForgotten = !word.isForgotten))
    }
}