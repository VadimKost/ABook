package com.vako.data.mapper.book

import com.vako.data.db.entity.book.AuthorEntity
import com.vako.data.db.entity.book.BookEntity
import com.vako.data.db.entity.book.ExternalVoiceoverEntity
import com.vako.data.db.entity.book.SeriesEntity
import com.vako.data.db.entity.book.VoiceoverEntity
import com.vako.data.db.entity.book.detailed.BookWithDetails
import com.vako.data.db.entity.book.detailed.VoiceoverWithDetails
import com.vako.data.parser.model.ParsedVoiceoverBookMetadata
import com.vako.domain.book.model.Book
import com.vako.domain.book.model.Series
import java.util.UUID

fun ParsedVoiceoverBookMetadata.toNewEntity(): BookWithDetails = toEntity(
    bookId = UUID.randomUUID().toString(),
    voiceoverId = UUID.randomUUID().toString()
)

fun ParsedVoiceoverBookMetadata.toEntity(
    bookId: String,
    voiceoverId: String
): BookWithDetails {
    return BookWithDetails(
        book = BookEntity(
            inAppId = bookId,
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
        voiceovers = listOf(
            VoiceoverWithDetails(
                voiceover = VoiceoverEntity(
                    id = voiceoverId,
                    bookId = bookId
                ),
                externalVoiceoverEntity = ExternalVoiceoverEntity(
                    voiceoverId = voiceoverId,
                    source = source,
                    externalId = relatedVoiceoverId
                ),
                readers = listOf(),
                mediaItems = listOf()
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