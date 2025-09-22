package com.vako.data.db.dao

import androidx.room.*
import com.vako.data.db.entity.book.*
import com.vako.data.db.entity.book.crossref.BookAuthorCrossRef
import com.vako.data.db.entity.book.crossref.VoiceoverReaderCrossRef
import com.vako.data.db.entity.book.detailed.BookWithDetails
import com.vako.data.db.entity.book.detailed.VoiceoverWithDetails

// TODO: Recheck and optimize
@Dao
interface BookDao {
    @Transaction
    @Query("SELECT * FROM Book WHERE inAppId = :inAppId")
    suspend fun getBookWithDetailsById(inAppId: String): BookWithDetails?

    @Transaction
    @Query("SELECT * FROM Book WHERE title = :title LIMIT 1")
    suspend fun getBookBySameTitle(title: String): BookWithDetails?

    @Query("SELECT COUNT(*) FROM Book")
    suspend fun getBookCount(): Int

    @Transaction
    @Query("""SELECT * FROM Book ORDER BY lastShownOrOpenedAt DESC LIMIT :limit""")
    suspend fun getRandomBooks(limit: Int): List<BookWithDetails>

    @Query("UPDATE Book SET lastShownOrOpenedAt = :timestamp WHERE inAppId IN (:bookIds)")
    suspend fun updateBooksLastShownOrOpenedTime(bookIds: List<String>, timestamp: Long = System.currentTimeMillis())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthors(authors: List<AuthorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReaders(readers: List<ReaderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoiceovers(voiceovers: List<VoiceoverEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItems(mediaItems: List<MediaItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookAuthorCrossRefs(crossRefs: List<BookAuthorCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoiceoverReaderCrossRefs(crossRefs: List<VoiceoverReaderCrossRef>)

    @Transaction
    suspend fun insertBookWithDetails(bookWithDetails: BookWithDetails) {
        insertBooks(listOf(bookWithDetails.book))
        insertAuthors(bookWithDetails.authors)

        val voiceoverEntities = mutableListOf<VoiceoverEntity>()
        val readerEntities = mutableListOf<ReaderEntity>()
        val mediaItemEntities = mutableListOf<MediaItemEntity>()
        val voiceoverReaderCrossRefs = mutableListOf<VoiceoverReaderCrossRef>()

        bookWithDetails.voiceovers.forEach { voiceover ->
            voiceoverEntities.add(voiceover.voiceover)
            readerEntities.addAll(voiceover.readers)
            mediaItemEntities.addAll(voiceover.mediaItems)
            voiceover.readers.forEach { reader ->
                voiceoverReaderCrossRefs.add(
                    VoiceoverReaderCrossRef(voiceover.voiceover.id, reader.id)
                )
            }
        }

        insertVoiceovers(voiceoverEntities)
        insertReaders(readerEntities)
        insertMediaItems(mediaItemEntities)
        insertVoiceoverReaderCrossRefs(voiceoverReaderCrossRefs)

        insertBookAuthorCrossRefs(
            bookWithDetails.authors.map {
                BookAuthorCrossRef(bookWithDetails.book.inAppId, it.id)
            }
        )
    }

    @Transaction
    suspend fun insertBookWithDetailsList(books: List<BookWithDetails>) {
        val allBooks = mutableListOf<BookEntity>()
        val allAuthors = mutableListOf<AuthorEntity>()
        val allVoiceovers = mutableListOf<VoiceoverEntity>()
        val allReaders = mutableListOf<ReaderEntity>()
        val allMediaItems = mutableListOf<MediaItemEntity>()
        val allBookAuthorRefs = mutableListOf<BookAuthorCrossRef>()
        val allVoiceoverReaderRefs = mutableListOf<VoiceoverReaderCrossRef>()

        books.forEach { bookWithDetails ->
            allBooks.add(bookWithDetails.book)
            allAuthors.addAll(bookWithDetails.authors)

            bookWithDetails.voiceovers.forEach { voiceover ->
                allVoiceovers.add(voiceover.voiceover)
                allReaders.addAll(voiceover.readers)
                allMediaItems.addAll(voiceover.mediaItems)
                voiceover.readers.forEach { reader ->
                    allVoiceoverReaderRefs.add(
                        VoiceoverReaderCrossRef(voiceover.voiceover.id, reader.id)
                    )
                }
            }

            bookWithDetails.authors.forEach { author ->
                allBookAuthorRefs.add(BookAuthorCrossRef(bookWithDetails.book.inAppId, author.id))
            }
        }

        insertBooks(allBooks)
        insertAuthors(allAuthors)
        insertVoiceovers(allVoiceovers)
        insertReaders(allReaders)
        insertMediaItems(allMediaItems)
        insertBookAuthorCrossRefs(allBookAuthorRefs)
        insertVoiceoverReaderCrossRefs(allVoiceoverReaderRefs)
    }

    @Transaction
    suspend fun addVoiceoversToBook(inAppId: String, newVoiceovers: List<VoiceoverWithDetails>) {
        val allVoiceovers = mutableListOf<VoiceoverEntity>()
        val allReaders = mutableListOf<ReaderEntity>()
        val allMediaItems = mutableListOf<MediaItemEntity>()
        val allVoiceoverReaderRefs = mutableListOf<VoiceoverReaderCrossRef>()

        newVoiceovers.forEach { voiceover ->
            allVoiceovers.add(voiceover.voiceover)
            allReaders.addAll(voiceover.readers)
            allMediaItems.addAll(voiceover.mediaItems)
            voiceover.readers.forEach { reader ->
                allVoiceoverReaderRefs.add(
                    VoiceoverReaderCrossRef(voiceover.voiceover.id, reader.id)
                )
            }
        }

        insertVoiceovers(allVoiceovers)
        insertReaders(allReaders)
        insertMediaItems(allMediaItems)
        insertVoiceoverReaderCrossRefs(allVoiceoverReaderRefs)
    }
}
