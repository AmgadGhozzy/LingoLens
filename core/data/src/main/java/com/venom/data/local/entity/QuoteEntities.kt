package com.venom.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "authors",
    indices = [
        Index(value = ["authorName"], unique = true)
    ]
)
data class AuthorEntity(
    @PrimaryKey(autoGenerate = true)
    val authorId: Long = 0,
    val authorName: String,
    val authorLink: String? = null,
    val authorBio: String? = null,
    val authorDescription: String? = null
)

@Entity(
    tableName = "tags",
    indices = [
        Index(value = ["tagName"], unique = true)
    ]
)
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val tagId: Long = 0,
    val tagName: String
)

@Entity(
    tableName = "quotes",
    indices = [
        Index(value = ["authorId"]),
        Index(value = ["isFavorite"]),
        Index(value = ["isRead"]),
        Index(value = ["quoteContent"]),
        Index(value = ["authorId", "isFavorite"]),
        Index(value = ["authorId", "isRead"]),
        Index(value = ["tagsString"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = AuthorEntity::class,
            parentColumns = ["authorId"],
            childColumns = ["authorId"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class QuoteEntity(
    @PrimaryKey(autoGenerate = true)
    val quoteId: Long = 0,
    val quoteContent: String,
    val authorId: Long? = null,
    val tagsString: String? = null,
    val isFavorite: Boolean = false,
    val isRead: Boolean = false
)
@Entity(
    tableName = "quoteTags",
    primaryKeys = ["quoteId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = QuoteEntity::class,
            parentColumns = ["quoteId"],
            childColumns = ["quoteId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["tagId"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["quoteId"]),
        Index(value = ["tagId"]),
        Index(value = ["quoteId", "tagId"])
    ]
)
data class QuoteTagCrossRef(
    val quoteId: Long,
    val tagId: Long
)

data class QuoteWithAuthorAndTags(
    @Embedded val quote: QuoteEntity,
    @Relation(
        parentColumn = "authorId",
        entityColumn = "authorId"
    )
    val author: AuthorEntity? = null,
    @Relation(
        parentColumn = "quoteId",
        entityColumn = "tagId",
        associateBy = Junction(
            value = QuoteTagCrossRef::class,
            parentColumn = "quoteId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity> = emptyList()
)

data class AuthorWithQuotes(
    @Embedded val author: AuthorEntity,
    @Relation(
        parentColumn = "authorId",
        entityColumn = "authorId"
    )
    val quotes: List<QuoteEntity> = emptyList()
)

data class TagWithQuotes(
    @Embedded val tag: TagEntity,
    @Relation(
        parentColumn = "tagId",
        entityColumn = "quoteId",
        associateBy = Junction(
            value = QuoteTagCrossRef::class,
            parentColumn = "tagId",
            entityColumn = "quoteId"
        )
    )
    val quotes: List<QuoteEntity> = emptyList()
)

// ADDITIONAL RELATION CLASSES FOR ADVANCED QUERIES
data class QuoteWithFullDetails(
    @Embedded val quote: QuoteEntity,
    @Relation(
        parentColumn = "authorId",
        entityColumn = "authorId"
    )
    val author: AuthorEntity? = null,
    @Relation(
        parentColumn = "quoteId",
        entityColumn = "tagId",
        associateBy = Junction(
            value = QuoteTagCrossRef::class,
            parentColumn = "quoteId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity> = emptyList()
) {
    val tagNames: List<String>
        get() = tags.map { it.tagName }

    val authorName: String
        get() = author?.authorName ?: "Unknown"
}

data class AuthorWithQuoteStats(
    @Embedded val author: AuthorEntity,
    @Relation(
        parentColumn = "authorId",
        entityColumn = "authorId"
    )
    val quotes: List<QuoteEntity> = emptyList()
) {
    val totalQuotes: Int
        get() = quotes.size

    val favoriteQuotes: Int
        get() = quotes.count { it.isFavorite }
}

data class TagWithQuoteStats(
    @Embedded val tag: TagEntity,
    @Relation(
        parentColumn = "tagId",
        entityColumn = "quoteId",
        associateBy = Junction(
            value = QuoteTagCrossRef::class,
            parentColumn = "tagId",
            entityColumn = "quoteId"
        )
    )
    val quotes: List<QuoteEntity> = emptyList()
) {
    val totalQuotes: Int
        get() = quotes.size

    val favoriteQuotes: Int
        get() = quotes.count { it.isFavorite }
}