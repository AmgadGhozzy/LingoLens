package com.venom.domain.model

data class Quote(
    val quoteId: Long = 0,
    val quoteContent: String,
    val authorId: Long = 0,
    val authorName: String = "",
    val tags: List<String> = emptyList(),
    val tagsString: String = "",
    val category: String = "",
    val isFavorite: Boolean = false,
    val isRead: Boolean = false
)

data class Author(
    val authorId: Long = 0,
    val authorName: String = "",
    val authorLink: String = "",
    val authorBio: String = "",
    val authorDescription: String = "",
    val quotesCount: Int = 0
)

data class Tag(
    val tagId: Long = 0,
    val tagName: String,
    val count: Int = 0
)

data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val colorLight: String,
    val colorDark: String
)

data class QuoteWithDetails(
    val quote: Quote,
    val author: Author,
    val tags: List<Tag>
)