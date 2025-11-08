package com.vako.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vako.data.db.dao.BookDao
import com.vako.data.db.dao.UserDao
import com.vako.data.db.entity.book.AuthorEntity
import com.vako.data.db.entity.book.BookEntity
import com.vako.data.db.entity.book.ExternalVoiceoverEntity
import com.vako.data.db.entity.book.MediaItemEntity
import com.vako.data.db.entity.book.ReaderEntity
import com.vako.data.db.entity.book.VoiceoverEntity
import com.vako.data.db.entity.book.crossref.BookAuthorCrossRef
import com.vako.data.db.entity.book.crossref.VoiceoverReaderCrossRef
import com.vako.data.db.entity.user.FavoriteBookEntity
import com.vako.data.db.entity.user.PlaybackProgressEntity
import com.vako.data.db.entity.user.PreferredVoiceoverEntity
import com.vako.data.db.entity.user.UserEntity

@Database(
    entities = [
        AuthorEntity::class,
        BookEntity::class,
        MediaItemEntity::class,
        ReaderEntity::class,
        VoiceoverEntity::class,
        ExternalVoiceoverEntity::class,
        BookAuthorCrossRef::class,
        VoiceoverReaderCrossRef::class,
        // user related
        UserEntity::class,
        PlaybackProgressEntity::class,
        FavoriteBookEntity::class,
        PreferredVoiceoverEntity::class,
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun userDao(): UserDao
}