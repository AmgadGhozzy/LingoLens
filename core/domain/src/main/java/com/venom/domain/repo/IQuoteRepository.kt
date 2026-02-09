package com.venom.domain.repo

import com.venom.domain.model.Author
import com.venom.domain.model.Quote
import com.venom.domain.model.QuoteWithDetails
import com.venom.domain.model.Tag
import kotlinx.coroutines.flow.Flow

interface IQuoteRepository {

    // QUOTE QUERIES (READ-ONLY)
    fun getAllQuotes(): Flow<List<Quote>>
    suspend fun getQuoteById(quoteId: Long): Quote?
    suspend fun getQuoteWithDetailsById(quoteId: Long): QuoteWithDetails?
    fun getFavoriteQuotes(): Flow<List<Quote>>
    fun getReadingListQuotes(): Flow<List<Quote>>
    fun getQuotesByAuthor(authorId: Long): Flow<List<Quote>>
    fun getQuotesByAuthorName(authorName: String): Flow<List<Quote>>
    fun getQuotesByTag(tagId: Long): Flow<List<Quote>>
    fun getQuotesByTagName(tagName: String): Flow<List<Quote>>
    fun getQuotesByMultipleAuthors(authorNames: List<String>): Flow<List<Quote>>
    fun getQuotesByMultipleTags(tagNames: List<String>): Flow<List<Quote>>
    fun searchQuotes(query: String): Flow<List<Quote>>
    suspend fun getRandomQuote(): Quote?

    // ADVANCED FILTERING
    fun getFilteredQuotes(
        authorName: String? = null,
        tagName: String? = null,
        category: String? = null,
        favoritesOnly: Boolean = false,
        readingListOnly: Boolean = false
    ): Flow<List<Quote>>

    // FAVORITE AND READING LIST OPERATIONS (ONLY ALLOWED MODIFICATIONS)
    suspend fun updateFavoriteStatus(quoteId: Long, isFavorite: Boolean)
    suspend fun updateReadStatus(quoteId: Long, isRead: Boolean)
    suspend fun updateMultipleFavoriteStatus(quoteIds: List<Long>, isFavorite: Boolean)
    suspend fun updateMultipleReadStatus(quoteIds: List<Long>, isRead: Boolean)

    // AUTHOR QUERIES (READ-ONLY)
    fun getAllAuthors(): Flow<List<Author>>
    suspend fun getAuthorById(authorId: Long): Author?
    suspend fun getAuthorByName(authorName: String): Author?
    fun searchAuthors(query: String): Flow<List<Author>>
    suspend fun getPopularAuthors(limit: Int = 10): List<Author>

    // TAG QUERIES (READ-ONLY)
    fun getAllTags(): Flow<List<Tag>>
    suspend fun getTagById(tagId: Long): Tag?
    suspend fun getTagByName(tagName: String): Tag?
    fun getTagsForQuote(quoteId: Long): Flow<List<Tag>>
    fun searchTags(query: String): Flow<List<Tag>>
    suspend fun getPopularTags(limit: Int = 10): List<Tag>

    // STATISTICS (READ-ONLY)
    suspend fun getTotalQuotesCount(): Int
    suspend fun getFavoriteQuotesCount(): Int
    suspend fun getReadQuoteCount(): Int
    suspend fun getQuoteCountForAuthor(authorId: Long): Int
    suspend fun getQuoteCountForTag(tagId: Long): Int
}