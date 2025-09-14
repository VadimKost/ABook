package com.vako.domain.book

import com.vako.domain.book.model.Book

interface BookRepository {
    fun getBookByInAppId(inAppId: String): Book
}