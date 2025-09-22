package com.vako.data.di;

import com.vako.data.repository.BookRepositoryImpl
import com.vako.domain.book.BookRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBookRepository(impl: BookRepositoryImpl): BookRepository
}
