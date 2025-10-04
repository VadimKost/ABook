package com.vako.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vako.data.db.dao.BookDao
import com.vako.data.db.entity.book.AuthorEntity
import com.vako.data.db.entity.book.BookEntity
import com.vako.data.db.entity.book.ExternalBookId
import com.vako.data.db.entity.book.MediaItemEntity
import com.vako.data.db.entity.book.ReaderEntity
import com.vako.data.db.entity.book.VoiceoverEntity
import com.vako.data.db.entity.book.crossref.BookAuthorCrossRef
import com.vako.data.db.entity.book.crossref.VoiceoverReaderCrossRef

@Database(
    entities = [
        AuthorEntity::class,
        BookEntity::class,
        MediaItemEntity::class,
        ReaderEntity::class,
        VoiceoverEntity::class,
        ExternalBookId::class,
        BookAuthorCrossRef::class,
        VoiceoverReaderCrossRef::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}