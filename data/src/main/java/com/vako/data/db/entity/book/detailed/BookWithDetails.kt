package com.vako.data.db.entity.book.detailed

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.vako.data.db.entity.book.*
import com.vako.data.db.entity.book.crossref.BookAuthorCrossRef

// Aggregates all book details, including authors, voiceovers (with readers and media items), and series

data class BookWithDetails(
    @Embedded val book: BookEntity,

    @Relation(
        parentColumn = "inAppId",
        entityColumn = "id",
        associateBy = Junction(
            value = BookAuthorCrossRef::class,
            parentColumn = "bookId",
            entityColumn = "authorId"
        )
    )
    val authors: List<AuthorEntity>,

    @Relation(
        entity = VoiceoverEntity::class,
        parentColumn = "inAppId",
        entityColumn = "bookId"
    )
    val voiceovers: List<VoiceoverWithDetails>
)
