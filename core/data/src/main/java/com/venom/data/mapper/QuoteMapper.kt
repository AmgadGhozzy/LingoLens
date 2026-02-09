package com.venom.data.mapper

import com.venom.data.local.entity.AuthorEntity
import com.venom.data.local.entity.AuthorWithQuotes
import com.venom.data.local.entity.QuoteWithAuthorAndTags
import com.venom.data.local.entity.TagEntity
import com.venom.data.local.entity.TagWithQuotes
import com.venom.domain.model.Author
import com.venom.domain.model.Quote
import com.venom.domain.model.Tag

fun QuoteWithAuthorAndTags.toQuote(): Quote {
    return Quote(
        quoteId = quote.quoteId,
        quoteContent = quote.quoteContent,
        authorId = quote.authorId ?: 0,
        authorName = author?.authorName ?: "Unknown",
        tags = tags.map { it.tagName },
        tagsString = quote.tagsString ?: "",
        isFavorite = quote.isFavorite,
        isRead = quote.isRead
    )
}

fun AuthorWithQuotes.toAuthor(): Author {
    return Author(
        authorId = author.authorId,
        authorName = author.authorName,
        authorLink = author.authorLink ?: "",
        authorBio = author.authorBio ?: "",
        authorDescription = author.authorDescription ?: "",
        quotesCount = quotes.size
    )
}

fun TagWithQuotes.toTag(): Tag {
    return Tag(
        tagId = tag.tagId,
        tagName = tag.tagName,
        count = quotes.size
    )
}

fun AuthorEntity.toAuthor(quotesCount: Int = 0): Author {
    return Author(
        authorId = authorId,
        authorName = authorName,
        authorLink = authorLink ?: "",
        authorBio = authorBio ?: "",
        authorDescription = authorDescription ?: "",
        quotesCount = quotesCount
    )
}

fun TagEntity.toTag(count: Int = 0): Tag {
    return Tag(
        tagId = tagId,
        tagName = tagName,
        count = count
    )
}
