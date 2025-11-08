package com.vako.data.parser

import com.vako.data.parser.model.ParsedVoiceoverBookMetadata
import com.vako.data.parser.model.ParsedVoiceover

abstract class BookParser {
    abstract val source: Source

    abstract suspend fun getRandomBooksMetadata(): List<ParsedVoiceoverBookMetadata>

    abstract suspend fun getBookMetadata(internalVoiceoverId: String): ParsedVoiceoverBookMetadata

    abstract suspend fun getVoiceover(internalVoiceoverId: String): ParsedVoiceover

    abstract suspend fun getAlternativeVoiceovers(internalVoiceoverId: String): List<ParsedVoiceover>

    abstract suspend fun getBooksInCycle(internalVoiceoverId: String): List<ParsedVoiceoverBookMetadata>
}

enum class Source(val baseUrl: String) {
    KnigaVUhe("https://knigavuhe.org")
}