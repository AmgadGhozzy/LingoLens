package com.venom.domain.repo

import com.venom.domain.model.Word
import com.venom.domain.model.WordLevels
import kotlinx.coroutines.flow.Flow

interface IWordRepository {
    fun getAllWords(): Flow<List<Word>>
    fun get10Words(): Flow<List<Word>>
    fun get10UnseenWords(): Flow<List<Word>>
    suspend fun getWordsFromLevel(
        level: WordLevels = WordLevels.Beginner,
        includeRemembered: Boolean = false,
        includeForgotten: Boolean = false,
        pageSize: Int = 10
    ): List<Word>

    suspend fun getLevelProgress(level: WordLevels): Float
    suspend fun getLevelsProgress(): Map<String, Float>
    fun getBookmarkedWords(): Flow<List<Word>>
    fun getRememberedWords(): Flow<List<Word>>
    fun getForgotedWords(): Flow<List<Word>>
    suspend fun toggleBookmark(word: Word)
    suspend fun toggleRemember(word: Word)
    suspend fun toggleForgot(word: Word)
}