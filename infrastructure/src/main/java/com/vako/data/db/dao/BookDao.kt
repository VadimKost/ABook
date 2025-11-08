package com.vako.data.db.dao

import androidx.room.*
import com.vako.data.db.entity.book.*
import com.vako.data.db.entity.book.crossref.BookAuthorCrossRef
import com.vako.data.db.entity.book.crossref.VoiceoverReaderCrossRef
import com.vako.data.db.entity.book.detailed.BookWithDetails
import com.vako.data.db.entity.book.detailed.VoiceoverWithDetails

/**
 * Data Access Object for Book and related entities.
 */
@Dao
interface BookDao {
    // region --- Queries ---
    @Transaction
    @Query("SELECT * FROM Book WHERE inAppId = :inAppId")
    suspend fun getBookWithDetailsById(inAppId: String): BookWithDetails?
    @Transaction
    @Query("SELECT * FROM Book WHERE title = :title LIMIT 1")
    suspend fun getBookBySameTitle(title: String): BookWithDetails?
    @Transaction
    @Query("""SELECT * FROM Book ORDER BY createdAt DESC LIMIT :limit""")
    suspend fun getRandomBooks(limit: Int): List<BookWithDetails>
    @Query("UPDATE Book SET modifiedAt = :modifiedAt WHERE inAppId = :inAppId")
    suspend fun updateBookModifiedAt(inAppId: String, modifiedAt: Long)

    // endregion

    // region --- Inserts ---

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExternalVoiceoverEntity(externalVoiceoverEntity: List<ExternalVoiceoverEntity>)

    // endregion

    // region --- Complex Insertions ---
    @Transaction
    suspend fun insertBookWithDetails(bookWithDetails: BookWithDetails) {
        insertBookWithDetailsList(listOf(bookWithDetails))
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
        val allExternalVoiceoverEntity = mutableListOf<ExternalVoiceoverEntity>()

        books.forEach { bookWithDetails ->
            allBooks.add(bookWithDetails.book)
            allAuthors.addAll(bookWithDetails.authors)

            bookWithDetails.voiceovers.forEach { voiceover ->
                allVoiceovers.add(voiceover.voiceover)
                allReaders.addAll(voiceover.readers)
                allMediaItems.addAll(voiceover.mediaItems)
                allExternalVoiceoverEntity.add(voiceover.externalVoiceoverEntity)
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
        insertBookAuthorCrossRefs(allBookAuthorRefs)
        insertReaders(allReaders)
        insertVoiceovers(allVoiceovers)
        insertMediaItems(allMediaItems)
        insertVoiceoverReaderCrossRefs(allVoiceoverReaderRefs)
        insertExternalVoiceoverEntity(allExternalVoiceoverEntity)
    }
    @Transaction
    suspend fun addVoiceoversToBook(inAppId: String, newVoiceovers: List<VoiceoverWithDetails>) {
        val allVoiceovers = mutableListOf<VoiceoverEntity>()
        val allReaders = mutableListOf<ReaderEntity>()
        val allMediaItems = mutableListOf<MediaItemEntity>()
        val allVoiceoverReaderRefs = mutableListOf<VoiceoverReaderCrossRef>()
        val allExternalVoiceoverEntity = mutableListOf<ExternalVoiceoverEntity>()

        newVoiceovers.forEach { voiceover ->
            allVoiceovers.add(voiceover.voiceover)
            allReaders.addAll(voiceover.readers)
            allMediaItems.addAll(voiceover.mediaItems)
            allExternalVoiceoverEntity.add(voiceover.externalVoiceoverEntity)
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
        insertExternalVoiceoverEntity(allExternalVoiceoverEntity)
    }
    // endregion
}
