package com.venom.data.repo

import com.venom.data.local.dao.QuoteDao
import com.venom.data.local.entity.AuthorWithQuotes
import com.venom.data.local.entity.TagWithQuotes
import com.venom.data.mapper.toAuthor
import com.venom.data.mapper.toQuote
import com.venom.data.mapper.toTag
import com.venom.domain.model.Author
import com.venom.domain.model.Quote
import com.venom.domain.model.QuoteWithDetails
import com.venom.domain.model.Tag
import com.venom.domain.repo.IQuoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuoteRepositoryImpl @Inject constructor(
    private val quoteDao: QuoteDao
) : IQuoteRepository {

    // QUOTE OPERATIONS
    override fun getAllQuotes(): Flow<List<Quote>> {
        return quoteDao.getAllQuotesWithDetails().map { entities ->
            entities.map { it.toQuote() }
        }
    }

    override suspend fun getQuoteById(quoteId: Long): Quote? {
        return quoteDao.getQuoteWithDetailsById(quoteId)?.toQuote()
    }

    override suspend fun getQuoteWithDetailsById(quoteId: Long): QuoteWithDetails? {
        return quoteDao.getQuoteWithDetailsById(quoteId)?.let { entity ->
            QuoteWithDetails(
                quote = entity.toQuote(),
                author = entity.author?.toAuthor() ?: Author(),
                tags = entity.tags.map { it.toTag() }
            )
        }
    }

    override fun getFavoriteQuotes(): Flow<List<Quote>> {
        return quoteDao.getFavoriteQuotesWithDetails().map { entities ->
            entities.map { it.toQuote() }
        }
    }

    override fun getReadingListQuotes(): Flow<List<Quote>> {
        return quoteDao.getReadingListQuotesWithDetails().map { entities ->
            entities.map { it.toQuote() }
        }
    }

    override fun getQuotesByAuthor(authorId: Long): Flow<List<Quote>> {
        return quoteDao.getQuotesWithDetailsByAuthor(authorId).map { entities ->
            entities.map { it.toQuote() }
        }
    }

    override fun getQuotesByAuthorName(authorName: String): Flow<List<Quote>> {
        return quoteDao.getAllQuotesWithDetails().map { entities ->
            entities.filter { it.author?.authorName.equals(authorName, ignoreCase = true) }
                .map { it.toQuote() }
        }
    }

    override fun getQuotesByTag(tagId: Long): Flow<List<Quote>> {
        return quoteDao.getQuotesWithDetailsByTag(tagId).map { entities ->
            entities.map { it.toQuote() }
        }
    }

    override fun getQuotesByTagName(tagName: String): Flow<List<Quote>> {
        return quoteDao.getAllQuotesWithDetails().map { entities ->
            entities.filter { entity ->
                entity.tags.any { it.tagName.equals(tagName, ignoreCase = true) }
            }.map { it.toQuote() }
        }
    }

    override fun getQuotesByMultipleAuthors(authorNames: List<String>): Flow<List<Quote>> {
        return quoteDao.getQuotesByMultipleAuthors(authorNames).map { entities ->
            entities.map { it.toQuote() }
        }
    }

    override fun getQuotesByMultipleTags(tagNames: List<String>): Flow<List<Quote>> {
        return quoteDao.getQuotesByMultipleTags(tagNames).map { entities ->
            entities.map { it.toQuote() }
        }
    }

    override fun searchQuotes(query: String): Flow<List<Quote>> {
        return quoteDao.searchQuotesWithDetails(query).map { entities ->
            entities.map { it.toQuote() }
        }
    }

    override suspend fun getRandomQuote(): Quote? {
        return quoteDao.getRandomQuoteWithDetails()?.toQuote()
    }

    override fun getFilteredQuotes(
        authorName: String?,
        tagName: String?,
        category: String?,
        favoritesOnly: Boolean,
        readingListOnly: Boolean
    ): Flow<List<Quote>> {
        return quoteDao.getFilteredQuotesWithDetails(
            authorName = authorName,
            tagName = tagName,
            favoritesOnly = favoritesOnly,
            readingListOnly = readingListOnly
        ).map { entities ->
            entities.map { it.toQuote() }
        }
    }

    // FAVORITE AND READING LIST OPERATIONS (ONLY ALLOWED MODIFICATIONS)
    override suspend fun updateFavoriteStatus(quoteId: Long, isFavorite: Boolean) {
        quoteDao.updateFavoriteStatus(quoteId, isFavorite)
    }

    override suspend fun updateReadStatus(quoteId: Long, isRead: Boolean) {
        quoteDao.updateIsReadStatus(quoteId, isRead)
    }

    override suspend fun updateMultipleFavoriteStatus(quoteIds: List<Long>, isFavorite: Boolean) {
        quoteDao.updateMultipleFavoriteStatus(quoteIds, isFavorite)
    }

    override suspend fun updateMultipleReadStatus(quoteIds: List<Long>, isRead: Boolean) {
        quoteDao.updateMultipleReadingListStatus(quoteIds, isRead)
    }

    // AUTHOR OPERATIONS
    override fun getAllAuthors(): Flow<List<Author>> {
        return quoteDao.getAllAuthorsWithQuotes().map { entities ->
            entities.map { it.toAuthor() }
        }
    }

    override suspend fun getAuthorById(authorId: Long): Author? {
        return quoteDao.getAuthorWithQuotesById(authorId)?.toAuthor()
    }

    override suspend fun getAuthorByName(authorName: String): Author? {
        val authors = mutableListOf<AuthorWithQuotes>()
        quoteDao.getAllAuthorsWithQuotes().collect { authorList ->
            authors.addAll(authorList)
        }
        return authors.find { it.author.authorName.equals(authorName, ignoreCase = true) }
            ?.toAuthor()
    }

    override fun searchAuthors(query: String): Flow<List<Author>> {
        return quoteDao.searchAuthorsWithQuotes(query).map { entities ->
            entities.map { it.toAuthor() }
        }
    }

    override suspend fun getPopularAuthors(limit: Int): List<Author> {
        return quoteDao.getPopularAuthors(limit).map { authorWithCount ->
            authorWithCount.author.toAuthor(authorWithCount.quoteCount)
        }
    }

    // TAG OPERATIONS
    override fun getAllTags(): Flow<List<Tag>> {
        return quoteDao.getAllTagsWithQuotes().map { entities ->
            entities.map { it.toTag() }
        }
    }

    override suspend fun getTagById(tagId: Long): Tag? {
        return quoteDao.getTagWithQuotesById(tagId)?.toTag()
    }

    override suspend fun getTagByName(tagName: String): Tag? {
        val tags = mutableListOf<TagWithQuotes>()
        quoteDao.getAllTagsWithQuotes().collect { tagList ->
            tags.addAll(tagList)
        }
        return tags.find { it.tag.tagName.equals(tagName, ignoreCase = true) }?.toTag()
    }

    override fun getTagsForQuote(quoteId: Long): Flow<List<Tag>> {
        return quoteDao.getTagsForQuote(quoteId).map { entities ->
            entities.map { it.toTag() }
        }
    }

    override fun searchTags(query: String): Flow<List<Tag>> {
        return quoteDao.searchTagsWithQuotes(query).map { entities ->
            entities.map { it.toTag() }
        }
    }

    override suspend fun getPopularTags(limit: Int): List<Tag> {
        return quoteDao.getPopularTags(limit).map { tagWithCount ->
            tagWithCount.tag.toTag(tagWithCount.quoteCount)
        }
    }

    // STATISTICS
    override suspend fun getTotalQuotesCount(): Int {
        return quoteDao.getTotalQuotesCount()
    }

    override suspend fun getFavoriteQuotesCount(): Int {
        return quoteDao.getFavoriteQuotesCount()
    }

    override suspend fun getReadQuoteCount(): Int {
        return quoteDao.getReadQuoteCount()
    }

    override suspend fun getQuoteCountForAuthor(authorId: Long): Int {
        return quoteDao.getQuoteCountForAuthor(authorId)
    }

    override suspend fun getQuoteCountForTag(tagId: Long): Int {
        return quoteDao.getQuoteCountForTag(tagId)
    }
}