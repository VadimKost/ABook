package com.vako.domain.book

import com.vako.domain.book.model.Book

interface BookRepository {
    suspend fun getBookByInAppId(inAppId: String): Book?
    suspend fun getRandomBooks(): List<Book>
}