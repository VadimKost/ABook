package com.vako.data.repository

import com.vako.data.db.dao.BookDao
import com.vako.data.mapper.BookMapper
import com.vako.data.parser.knigavuhe.KnigaVUheParser
import com.vako.domain.book.BookRepository
import com.vako.domain.book.model.Book
import com.vako.domain.book.model.Voiceover
import javax.inject.Inject
import javax.inject.Singleton

private const val BOOKS_PER_PAGE = 10
@Singleton
class BookRepositoryImpl @Inject constructor(
    private val knigaVUheParser: KnigaVUheParser,
    private val bookDao: BookDao,
    private val bookMapper: BookMapper
) : BookRepository {

    override suspend fun getBookByInAppId(inAppId: String): Book? {
        return bookDao.getBookWithDetailsById(inAppId)?.let { bookMapper.toDomain(it) }
    }

    // TODO: Fix copies of books in DB
    override suspend fun getRandomBooks(): List<Book> {
        val newRandomBooks = knigaVUheParser.getRandomBooks()
        val booksToInsert = newRandomBooks
            .filter { parsedBook ->
                bookDao.getBookBySameTitle(parsedBook.title) == null
            }
            .map { bookMapper.fromParsedBook(it) }

        if (booksToInsert.isNotEmpty()) {
            bookDao.insertBookWithDetailsList(booksToInsert)
        }

        val randomBooks = bookDao.getRandomBooks(limit = BOOKS_PER_PAGE)

        return randomBooks.map { bookMapper.toDomain(it) }
    }

    override suspend fun addVoiceoversToBook(
        inAppId: String,
        newVoiceovers: List<Voiceover>
    ): Book? {
        TODO()
        /*        // Check if book exists first
                if (bookDao.getBookWithDetailsById(inAppId) == null) return null

                // Convert and add all voiceovers in a single transaction
                val voiceoverDetails = newVoiceovers.map { voiceoverMapper.toEntity(it) }
                    .onEach { it.voiceover.bookId = inAppId }

                bookDao.addVoiceoversToBook(inAppId, voiceoverDetails)

                // Update lastShownOrOpenedAt and return updated book
                bookDao.updateBooksLastShownOrOpenedTime(listOf(inAppId))
                return bookDao.getBookWithDetailsById(inAppId)?.let { bookMapper.toDomain(it) }*/
    }

}
