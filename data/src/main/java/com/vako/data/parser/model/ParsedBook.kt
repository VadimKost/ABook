package com.vako.data.parser.model

data class ParsedBook(
    val internalId: String,
    val title: String,
    val authors: List<String>,
    val series: String?,
    val seriesIndex: Int?,
    val coverUrl: String,
)

data class ParsedVoiceover(
    val internalId: String,
    val readers: List<String>,
    val mediaItems: List<ParsedMediaItem>
)

data class ParsedMediaItem(
    val url: String,
    val title: String,
    val duration: Long,
)