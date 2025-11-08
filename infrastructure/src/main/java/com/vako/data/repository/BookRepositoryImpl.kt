package com.vako.data.repository

import com.vako.data.db.dao.BookDao
import com.vako.data.db.entity.book.detailed.BookWithDetails
import com.vako.data.db.entity.book.detailed.VoiceoverWithDetails
import com.vako.data.mapper.book.toDomain
import com.vako.data.mapper.book.toNewEntity
import com.vako.data.mapper.book.toVoiceoverWithDetails
import com.vako.data.parser.BookParser
import com.vako.data.parser.model.ParsedVoiceover
import com.vako.data.parser.model.ParsedVoiceoverBookMetadata
import com.vako.data.repository.policy.BookDetailingUpdatePolicy
import com.vako.domain.book.BookRepository
import com.vako.domain.book.model.Book
import java.net.URL
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val BOOKS_PER_PAGE = 10

/*Note: Later will be more parsers added, so the repository implementation will be adjusted accordingly.*/
@Singleton
class BookRepositoryImpl @Inject constructor(
    private val bookParsers: Set<@JvmSuppressWildcards BookParser>,
    private val bookDao: BookDao,
) : BookRepository {

    override suspend fun getBookByInAppId(inAppId: String): Book? {
        var cached = bookDao.getBookWithDetailsById(inAppId) ?: return null
        if (!BookDetailingUpdatePolicy.shouldUpdate(cached)) {
            return cached.toDomain()
        }

        val newVoiceovers = fetchNewVoiceoversForBook(cached)

        if (newVoiceovers.isNotEmpty()) {
            bookDao.addVoiceoversToBook(inAppId, newVoiceovers)
            bookDao.updateBookModifiedAt(inAppId, System.currentTimeMillis())
            cached = bookDao.getBookWithDetailsById(inAppId) ?: cached
        }

        return cached.toDomain()
    }

    // TODO: Make better and check 
    suspend fun fetchNewVoiceoversForBook(
        cachedBook: BookWithDetails
    ): List<VoiceoverWithDetails> {

        val uniqueBySource = cachedBook.voiceovers
            .distinctBy { it.externalVoiceoverEntity.source }

        val cachedExternal = cachedBook.voiceovers
            .map { it.externalVoiceoverEntity }

        return uniqueBySource.flatMap { voiceover ->
            val parsed = fetchVoiceoversFromSite(voiceover)

            parsed.map { parsed ->

                val existing = cachedExternal
                    .firstOrNull {
                        it.source == parsed.source &&
                                it.externalId == parsed.internalId
                    }

                val id = existing?.voiceoverId ?: UUID.randomUUID().toString()

                parsed.toVoiceoverWithDetails(
                    inAppId = cachedBook.book.inAppId,
                    voiceoverId = id
                )
            }
        }
    }

    private suspend fun fetchVoiceoversFromSite(voiceover: VoiceoverWithDetails): List<ParsedVoiceover> {

        val ext = voiceover.externalVoiceoverEntity
        val parser = bookParsers.first { it.source == ext.source }

        val known = parser.getVoiceover(ext.externalId)
        val alt = parser.getAlternativeVoiceovers(ext.externalId)

        return alt + known
    }

    override suspend fun getBookByUrl(url: String): Book? {
        val urlObject = URL(url)
        val baseUrl = "${urlObject.protocol}://${urlObject.host}"
        val internalId = url
            .trimEnd('/')
            .substringAfterLast('/')
        val parser = bookParsers.first { it.source.baseUrl == baseUrl }
        val parsedBook = parser.getBookMetadata(internalId)
        val bookWithSameTitle = bookDao.getBookBySameTitle(parsedBook.title)

        if (bookWithSameTitle == null) {
            val bookEntity = parsedBook.toNewEntity()
            bookDao.insertBookWithDetails(bookEntity)
            return bookEntity.toDomain()
        } else {
            return bookWithSameTitle.toDomain()
        }
    }

    // TODO: Fix copies of books in DB
    // TODO: we get just book and cant get voiceovers
    override suspend fun getRandomBooks(): List<Book> {
        val newRandomBooks = bookParsers.first().getRandomBooksMetadata()
        val uniqueBooks = getUniqueBook(newRandomBooks)

        if (uniqueBooks.isNotEmpty()) {
            bookDao.insertBookWithDetailsList(uniqueBooks.map { it.toNewEntity() })
        }

        val randomBooks = bookDao.getRandomBooks(limit = BOOKS_PER_PAGE)

        return randomBooks.map { it.toDomain() }
    }

    suspend fun getUniqueBook(books: List<ParsedVoiceoverBookMetadata>): List<ParsedVoiceoverBookMetadata> {
        return books.filter { parsedBook ->
            bookDao.getBookBySameTitle(parsedBook.title) == null
        }
    }


}
