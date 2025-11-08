package com.vako.data.db.entity.book.crossref

import androidx.room.Entity
import androidx.room.ForeignKey
import com.vako.data.db.entity.book.AuthorEntity
import com.vako.data.db.entity.book.BookEntity

@Entity(
    tableName = "BookAuthorCrossRef",
    primaryKeys = ["bookId", "authorId"],
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["inAppId"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AuthorEntity::class,
            parentColumns = ["id"],
            childColumns = ["authorId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BookAuthorCrossRef(
    val bookId: String,
    val authorId: String
)
