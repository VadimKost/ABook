package com.vako.data.di

import android.content.Context
import androidx.room.Room
import com.vako.data.db.AppDatabase
import com.vako.data.db.dao.BookDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class StorageModule {

    @Provides
    @Singleton
    fun provideDB(@ApplicationContext context: Context): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "ABookDB"
    ).build()

    @Provides
    fun provideBookDao(database: AppDatabase): BookDao = database.bookDao()

}
