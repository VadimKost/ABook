package com.vako.data.mapper

import com.vako.data.db.entity.book.*
import com.vako.data.db.entity.book.detailed.BookWithDetails
import com.vako.data.mapper.base.ListEntityMapper
import com.vako.data.parser.model.ParsedBook
import com.vako.domain.book.model.Author
import com.vako.domain.book.model.Book
import com.vako.domain.book.model.Series
import java.util.UUID
import javax.inject.Inject

class BookMapper @Inject constructor(
    private val authorMapper: AuthorMapper,
    private val voiceoverMapper: VoiceoverMapper
) : ListEntityMapper<BookWithDetails, Book> {

    override fun toDomain(entity: BookWithDetails): Book = Book(
        inAppId = entity.book.inAppId,
        title = entity.book.title,
        cover = entity.book.cover,
        authors = entity.authors.map { Author(it.fullName) },
        series = entity.book.series?.let { Series(it.name, it.numberInCycle) },
        voiceovers = entity.voiceovers.map { voiceoverMapper.toDomain(it) }
    )

    override fun toEntity(domain: Book): BookWithDetails {
        val bookId = UUID.randomUUID().toString()
        return BookWithDetails(
            book = BookEntity(
                inAppId = bookId,
                title = domain.title,
                cover = domain.cover,
                series = domain.series?.let {
                    SeriesEntity(name = it.name, numberInCycle = it.seriesIndex)
                }
            ),
            authors = domain.authors.map { author ->
                AuthorEntity(
                    id = UUID.randomUUID().toString(),
                    fullName = author.fullName
                )
            },
            voiceovers = domain.voiceovers.map {
                voiceoverMapper.toEntity(it).apply {
                    voiceover.bookId = bookId
                }
            }
        )
    }

    fun fromParsedBook(parsedBook: ParsedBook): BookWithDetails {
        val bookId = UUID.randomUUID().toString()
        return BookWithDetails(
            book = BookEntity(
                inAppId = bookId,
                title = parsedBook.title,
                cover = parsedBook.coverUrl,
                series = parsedBook.series?.let {
                    SeriesEntity(name = it, numberInCycle = parsedBook.seriesIndex ?: 0)
                }
            ),
            authors = parsedBook.authors.map { authorName ->
                AuthorEntity(
                    id = UUID.randomUUID().toString(),
                    fullName = authorName
                )
            },
            voiceovers = emptyList()
        )
    }
}
