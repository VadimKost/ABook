package com.vako.data.mapper.book

import com.vako.data.db.entity.book.AuthorEntity
import com.vako.data.db.entity.book.BookEntity
import com.vako.data.db.entity.book.ExternalBookId
import com.vako.data.db.entity.book.SeriesEntity
import com.vako.data.db.entity.book.detailed.BookWithDetails
import com.vako.data.parser.model.ParsedBook
import com.vako.domain.book.model.Book
import com.vako.domain.book.model.Series
import java.util.UUID

fun ParsedBook.toEntity(): BookWithDetails {
    val bookId = UUID.randomUUID().toString()
    return BookWithDetails(
        book = BookEntity(
            title = this.title,
            cover = this.coverUrl,
            series = this.series?.let {
                SeriesEntity(name = it, numberInCycle = this.seriesIndex ?: 0)
            }
        ),
        authors = this.authors.map { authorName ->
            AuthorEntity(
                id = UUID.randomUUID().toString(),
                fullName = authorName
            )
        },
        voiceovers = emptyList(),
        externalBookIds = listOf(
            ExternalBookId(
                inAppBookId = bookId,
                source = this.source,
                externalId = this.internalId
            )
        )
    )
}

fun BookWithDetails.toDomain(): Book {
    return Book(
        inAppId = this.book.inAppId,
        title = this.book.title,
        cover = this.book.cover,
        authors = this.authors.map { it.toDomain() },
        series = this.book.series?.let { Series(it.name, it.numberInCycle) },
        voiceovers = this.voiceovers.map { it.toDomain() }
    )
}