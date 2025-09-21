package com.vako.data.parser

import com.vako.data.parser.model.ParsedBook
import com.vako.data.parser.model.ParsedVoiceover

abstract class BooksParser {
    abstract val source: Source

    abstract suspend fun getRandomBooks(): List<ParsedBook>

    abstract suspend fun getBook(internalBookId: Int): ParsedBook

    abstract suspend fun getBookVoiceovers(internalVoiceoverId: String): List<ParsedVoiceover>

    abstract suspend fun getBooksInCycle(internalBookId: String): ParsedBook
}

enum class Source(val baseUrl: String) {
    KnigaVUhe("https://knigavuhe.org")
}