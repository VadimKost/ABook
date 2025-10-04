package com.vako.data.repository

import com.vako.data.db.dao.BookDao
import com.vako.data.mapper.book.toDomain
import com.vako.data.mapper.book.toEntity
import com.vako.data.mapper.book.toVoiceoverWithDetails
import com.vako.data.parser.knigavuhe.KnigaVUheParser
import com.vako.data.repository.policy.BookDetailingUpdatePolicy
import com.vako.domain.book.BookRepository
import com.vako.domain.book.model.Book
import javax.inject.Inject
import javax.inject.Singleton

private const val BOOKS_PER_PAGE = 10

@Singleton
class BookRepositoryImpl @Inject constructor(
    private val knigaVUheParser: KnigaVUheParser,
    private val bookDao: BookDao,
) : BookRepository {

    override suspend fun getBookByInAppId(inAppId: String): Book? {
        var cachedBook = bookDao.getBookWithDetailsById(inAppId)
        if (cachedBook != null && BookDetailingUpdatePolicy.shouldUpdate(cachedBook)) {
            val parsedVoiceovers =
                knigaVUheParser.getBookVoiceovers(cachedBook.externalBookIds.first().externalId)
            val mappedVoiceovers = parsedVoiceovers.map { it.toVoiceoverWithDetails(inAppId) }
            bookDao.addVoiceoversToBook(inAppId, mappedVoiceovers)
            cachedBook = bookDao.getBookWithDetailsById(inAppId)
        }
        return cachedBook?.toDomain()
    }

    // TODO: Fix copies of books in DB
    override suspend fun getRandomBooks(): List<Book> {
        val newRandomBooks = knigaVUheParser.getRandomBooks()
        val booksToInsert = newRandomBooks
            .filter { parsedBook ->
                bookDao.getBookBySameTitle(parsedBook.title) == null
            }
            .map { it.toEntity() }

        if (booksToInsert.isNotEmpty()) {
            bookDao.insertBookWithDetailsList(booksToInsert)
        }

        val randomBooks = bookDao.getRandomBooks(limit = BOOKS_PER_PAGE)

        return randomBooks.map { it.toDomain() }
    }
}
