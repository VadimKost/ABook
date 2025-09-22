package com.vako.domain.book

import com.vako.domain.book.model.Book
import com.vako.domain.book.model.Voiceover

interface BookRepository {
    suspend fun getBookByInAppId(inAppId: String): Book?
    suspend fun getRandomBooks(): List<Book>
    suspend fun addVoiceoversToBook(inAppId: String, newVoiceovers: List<Voiceover>): Book?
}