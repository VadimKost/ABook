package com.vako.data.db.entity.book.crossref

import androidx.room.Entity

@Entity(
    tableName = "BookAuthorCrossRef",
    primaryKeys = ["bookId", "authorId"]
)
data class BookAuthorCrossRef(
    val bookId: String,
    val authorId: String
)
