package com.vako.data.di

import com.vako.data.repository.BookRepositoryImpl
import com.vako.data.repository.UserRepositoryImpl
import com.vako.domain.book.BookRepository
import com.vako.domain.user.UserRepository
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

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
