package com.venom.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import androidx.room.Transaction
import com.venom.data.local.entity.AuthorEntity
import com.venom.data.local.entity.AuthorWithQuotes
import com.venom.data.local.entity.QuoteWithAuthorAndTags
import com.venom.data.local.entity.TagEntity
import com.venom.data.local.entity.TagWithQuotes
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {

    // BASIC QUOTE QUERIES WITH RELATIONS
    @Transaction
    @Query("SELECT * FROM quotes ORDER BY quoteId DESC")
    fun getAllQuotesWithDetails(): Flow<List<QuoteWithAuthorAndTags>>

    @Transaction
    @Query("SELECT * FROM quotes WHERE quoteId = :quoteId")
    suspend fun getQuoteWithDetailsById(quoteId: Long): QuoteWithAuthorAndTags?

    @Transaction
    @Query("SELECT * FROM quotes WHERE isFavorite = 1 ORDER BY quoteId DESC")
    fun getFavoriteQuotesWithDetails(): Flow<List<QuoteWithAuthorAndTags>>

    @Transaction
    @Query("SELECT * FROM quotes WHERE isRead = 1 ORDER BY quoteId DESC")
    fun getReadingListQuotesWithDetails(): Flow<List<QuoteWithAuthorAndTags>>

    @Transaction
    @Query("SELECT * FROM quotes WHERE authorId = :authorId ORDER BY quoteId DESC")
    fun getQuotesWithDetailsByAuthor(authorId: Long): Flow<List<QuoteWithAuthorAndTags>>

    @Transaction
    @Query(
        """
        SELECT q.* FROM quotes q
        INNER JOIN quoteTags qt ON q.quoteId = qt.quoteId
        WHERE qt.tagId = :tagId
        ORDER BY q.quoteId DESC
    """
    )
    fun getQuotesWithDetailsByTag(tagId: Long): Flow<List<QuoteWithAuthorAndTags>>

    @Transaction
    @Query("SELECT * FROM quotes ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuoteWithDetails(): QuoteWithAuthorAndTags?

    // OPTIMIZED SEARCH QUERIES
    @Transaction
    @Query(
        """
        SELECT DISTINCT q.* FROM quotes q
        LEFT JOIN authors a ON q.authorId = a.authorId
        LEFT JOIN quoteTags qt ON q.quoteId = qt.quoteId
        LEFT JOIN tags t ON qt.tagId = t.tagId
        WHERE q.quoteContent LIKE '%' || :query || '%'
           OR a.authorName LIKE '%' || :query || '%'
           OR t.tagName LIKE '%' || :query || '%'
        ORDER BY 
            CASE 
                WHEN q.quoteContent LIKE :query || '%' THEN 1
                WHEN a.authorName LIKE :query || '%' THEN 2
                WHEN t.tagName LIKE :query || '%' THEN 3
                WHEN q.quoteContent LIKE '%' || :query || '%' THEN 4
                WHEN a.authorName LIKE '%' || :query || '%' THEN 5
                ELSE 6 
            END,
            q.quoteId DESC
        LIMIT 100
    """
    )
    fun searchQuotesWithDetails(query: String): Flow<List<QuoteWithAuthorAndTags>>

    @Transaction
    @Query(
        """
        SELECT DISTINCT q.* FROM quotes q
        LEFT JOIN authors a ON q.authorId = a.authorId
        LEFT JOIN quoteTags qt ON q.quoteId = qt.quoteId
        LEFT JOIN tags t ON qt.tagId = t.tagId
        WHERE q.quoteContent LIKE :exactQuery
           OR a.authorName LIKE :exactQuery
           OR t.tagName LIKE :exactQuery
        ORDER BY q.quoteId DESC
        LIMIT 50
    """
    )
    fun searchQuotesExact(exactQuery: String): Flow<List<QuoteWithAuthorAndTags>>

    // MULTI-SELECTION FILTERING QUERIES
    @Transaction
    @Query(
        """
        SELECT DISTINCT q.* FROM quotes q
        INNER JOIN authors a ON q.authorId = a.authorId
        WHERE a.authorName IN (:authorNames)
        AND (:favoritesOnly = 0 OR q.isFavorite = 1)
        AND (:readingListOnly = 0 OR q.isRead = 1)
        ORDER BY q.quoteId DESC
        LIMIT :limit
    """
    )
    fun getQuotesByMultipleAuthors(
        authorNames: List<String>,
        favoritesOnly: Boolean = false,
        readingListOnly: Boolean = false,
        limit: Int = 1000
    ): Flow<List<QuoteWithAuthorAndTags>>

    @Transaction
    @Query(
        """
        SELECT DISTINCT q.* FROM quotes q
        INNER JOIN quoteTags qt ON q.quoteId = qt.quoteId
        INNER JOIN tags t ON qt.tagId = t.tagId
        WHERE t.tagName IN (:tagNames)
        AND (:favoritesOnly = 0 OR q.isFavorite = 1)
        AND (:readingListOnly = 0 OR q.isRead = 1)
        GROUP BY q.quoteId, q.quoteContent, q.authorId, q.tagsString, q.isFavorite, q.isRead
        HAVING COUNT(DISTINCT t.tagName) >= :minTagMatches
        ORDER BY q.quoteId DESC
        LIMIT :limit
    """
    )
    fun getQuotesByMultipleTags(
        tagNames: List<String>,
        minTagMatches: Int = 1, // 1 for OR logic, tagNames.size for AND logic
        favoritesOnly: Boolean = false,
        readingListOnly: Boolean = false,
        limit: Int = 1000
    ): Flow<List<QuoteWithAuthorAndTags>>

    // COMPLEX FILTERING WITH MULTIPLE AUTHORS AND TAGS
    @Transaction
    @Query(
        """
        SELECT DISTINCT q.* FROM quotes q
        LEFT JOIN authors a ON q.authorId = a.authorId
        LEFT JOIN quoteTags qt ON q.quoteId = qt.quoteId
        LEFT JOIN tags t ON qt.tagId = t.tagId
        WHERE 1=1
        AND (CASE 
            WHEN :hasAuthors = 1 THEN a.authorName IN (:authorNames)
            ELSE 1=1 END)
        AND (CASE 
            WHEN :hasTags = 1 THEN t.tagName IN (:tagNames)
            ELSE 1=1 END)
        AND (:favoritesOnly = 0 OR q.isFavorite = 1)
        AND (:readingListOnly = 0 OR q.isRead = 1)
        GROUP BY q.quoteId, q.quoteContent, q.authorId, q.tagsString, q.isFavorite, q.isRead
        HAVING 1=1
        AND (CASE 
            WHEN :hasTags = 1 THEN COUNT(DISTINCT t.tagName) >= 1
            ELSE 1=1 END)
        ORDER BY q.quoteId DESC
        LIMIT :limit
    """
    )
    fun getComplexFilteredQuotes(
        authorNames: List<String> = emptyList(),
        tagNames: List<String> = emptyList(),
        hasAuthors: Boolean = authorNames.isNotEmpty(),
        hasTags: Boolean = tagNames.isNotEmpty(),
        favoritesOnly: Boolean = false,
        readingListOnly: Boolean = false,
        limit: Int = 1000
    ): Flow<List<QuoteWithAuthorAndTags>>


    // AUTHOR QUERIES WITH RELATIONS AND OPTIMIZATION
    @Transaction
    @Query("SELECT * FROM authors ORDER BY authorName ASC")
    fun getAllAuthorsWithQuotes(): Flow<List<AuthorWithQuotes>>

    @Transaction
    @Query("SELECT * FROM authors WHERE authorId = :authorId")
    suspend fun getAuthorWithQuotesById(authorId: Long): AuthorWithQuotes?

    @Transaction
    @Query(
        """
        SELECT * FROM authors 
        WHERE authorName LIKE '%' || :query || '%' 
        ORDER BY 
            CASE WHEN authorName LIKE :query || '%' THEN 1 ELSE 2 END,
            authorName ASC
        LIMIT 20
    """
    )
    fun searchAuthorsWithQuotes(query: String): Flow<List<AuthorWithQuotes>>

    // TAG QUERIES WITH RELATIONS AND OPTIMIZATION
    @Transaction
    @Query("SELECT * FROM tags ORDER BY tagName ASC")
    fun getAllTagsWithQuotes(): Flow<List<TagWithQuotes>>

    @Transaction
    @Query("SELECT * FROM tags WHERE tagId = :tagId")
    suspend fun getTagWithQuotesById(tagId: Long): TagWithQuotes?

    @Transaction
    @Query(
        """
        SELECT * FROM tags 
        WHERE tagName LIKE '%' || :query || '%' 
        ORDER BY 
            CASE WHEN tagName LIKE :query || '%' THEN 1 ELSE 2 END,
            tagName ASC
        LIMIT 20
    """
    )
    fun searchTagsWithQuotes(query: String): Flow<List<TagWithQuotes>>

    @Transaction
    @Query(
        """
        SELECT t.* FROM tags t
        INNER JOIN quoteTags qt ON t.tagId = qt.tagId
        WHERE qt.quoteId = :quoteId
        ORDER BY t.tagName ASC
    """
    )
    fun getTagsForQuote(quoteId: Long): Flow<List<TagWithQuotes>>

    // STATISTICS QUERIES WITH OPTIMIZATION
    @Query("SELECT COUNT(*) FROM quotes")
    suspend fun getTotalQuotesCount(): Int

    @Query("SELECT COUNT(*) FROM quotes WHERE isFavorite = 1")
    suspend fun getFavoriteQuotesCount(): Int

    @Query("SELECT COUNT(*) FROM quotes WHERE isRead = 1")
    suspend fun getReadQuoteCount(): Int

    @Query("SELECT COUNT(*) FROM quotes WHERE authorId = :authorId")
    suspend fun getQuoteCountForAuthor(authorId: Long): Int

    @Query(
        """
        SELECT COUNT(DISTINCT q.quoteId) FROM quotes q
        INNER JOIN quoteTags qt ON q.quoteId = qt.quoteId
        WHERE qt.tagId = :tagId
    """
    )
    suspend fun getQuoteCountForTag(tagId: Long): Int

    @Query(
        """
        SELECT COUNT(DISTINCT q.quoteId) FROM quotes q
        INNER JOIN authors a ON q.authorId = a.authorId
        WHERE a.authorName IN (:authorNames)
    """
    )
    suspend fun getQuoteCountByAuthors(authorNames: List<String>): Int

    @Query(
        """
        SELECT COUNT(DISTINCT q.quoteId) FROM quotes q
        INNER JOIN quoteTags qt ON q.quoteId = qt.quoteId
        INNER JOIN tags t ON qt.tagId = t.tagId
        WHERE t.tagName IN (:tagNames)
    """
    )
    suspend fun getQuoteCountByTags(tagNames: List<String>): Int

    // FAVORITE AND READING LIST OPERATIONS (ONLY ALLOWED MODIFICATIONS)
    @Query("UPDATE quotes SET isFavorite = :isFavorite WHERE quoteId = :quoteId")
    suspend fun updateFavoriteStatus(quoteId: Long, isFavorite: Boolean)

    @Query("UPDATE quotes SET isRead = :isRead WHERE quoteId = :quoteId")
    suspend fun updateIsReadStatus(quoteId: Long, isRead: Boolean)

    @Query("UPDATE quotes SET isFavorite = :isFavorite WHERE quoteId IN (:quoteIds)")
    suspend fun updateMultipleFavoriteStatus(quoteIds: List<Long>, isFavorite: Boolean)

    @Query("UPDATE quotes SET isRead = :isRead WHERE quoteId IN (:quoteIds)")
    suspend fun updateMultipleReadingListStatus(quoteIds: List<Long>, isRead: Boolean)

    // ADVANCED FILTERING WITH RELATIONS
    @Transaction
    @Query(
        """
        SELECT DISTINCT q.* FROM quotes q
        LEFT JOIN authors a ON q.authorId = a.authorId
        LEFT JOIN quoteTags qt ON q.quoteId = qt.quoteId
        LEFT JOIN tags t ON qt.tagId = t.tagId
        WHERE (:authorName IS NULL OR a.authorName = :authorName)
          AND (:tagName IS NULL OR t.tagName = :tagName)
          AND (:favoritesOnly = 0 OR q.isFavorite = 1)
          AND (:readingListOnly = 0 OR q.isRead = 1)
        ORDER BY q.quoteId DESC
    """
    )
    fun getFilteredQuotesWithDetails(
        authorName: String? = null,
        tagName: String? = null,
        favoritesOnly: Boolean = false,
        readingListOnly: Boolean = false
    ): Flow<List<QuoteWithAuthorAndTags>>

    // GET QUOTES BY MULTIPLE AUTHORS
    @Transaction
    @Query(
        """
        SELECT q.* FROM quotes q
        INNER JOIN authors a ON q.authorId = a.authorId
        WHERE a.authorName IN (:authorNames)
        ORDER BY q.quoteId DESC
    """
    )
    fun getQuotesByMultipleAuthors(authorNames: List<String>): Flow<List<QuoteWithAuthorAndTags>>

    // GET QUOTES BY MULTIPLE TAGS
    @Transaction
    @Query(
        """
        SELECT DISTINCT q.* FROM quotes q
        INNER JOIN quoteTags qt ON q.quoteId = qt.quoteId
        INNER JOIN tags t ON qt.tagId = t.tagId
        WHERE t.tagName IN (:tagNames)
        ORDER BY q.quoteId DESC
    """
    )
    fun getQuotesByMultipleTags(tagNames: List<String>): Flow<List<QuoteWithAuthorAndTags>>

    // GET POPULAR AUTHORS (by quote count)
    @Query(
        """
        SELECT a.*, COUNT(q.quoteId) as quoteCount
        FROM authors a
        LEFT JOIN quotes q ON a.authorId = q.authorId
        GROUP BY a.authorId, a.authorName, a.authorLink, a.authorBio, a.authorDescription
        ORDER BY quoteCount DESC, a.authorName ASC
        LIMIT :limit
    """
    )
    suspend fun getPopularAuthors(limit: Int = 10): List<AuthorWithQuoteCount>

    // GET POPULAR TAGS (by quote count)
    @Query(
        """
        SELECT t.*, COUNT(qt.quoteId) as quoteCount
        FROM tags t
        LEFT JOIN quoteTags qt ON t.tagId = qt.tagId
        GROUP BY t.tagId, t.tagName
        ORDER BY quoteCount DESC, t.tagName ASC
        LIMIT :limit
    """
    )
    suspend fun getPopularTags(limit: Int = 10): List<TagWithQuoteCount>
}

// Additional data classes for statistics
data class AuthorWithQuoteCount(
    @Embedded val author: AuthorEntity,
    val quoteCount: Int
)

data class TagWithQuoteCount(
    @Embedded val tag: TagEntity,
    val quoteCount: Int
)