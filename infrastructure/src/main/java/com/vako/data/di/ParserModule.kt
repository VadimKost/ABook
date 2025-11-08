package com.vako.data.di

import com.google.gson.Gson
import com.vako.data.parser.BookParser
import com.vako.data.parser.knigavuhe.KnigaVUheParser
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ParserModule {

    @Binds
    @IntoSet
    abstract fun bindKnigaVUhe(parser: KnigaVUheParser): BookParser

    companion object {
        @Provides
        @Singleton
        fun provideGson(): Gson = Gson()
    }
}
