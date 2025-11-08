package com.vako.data.parser.model

import com.vako.data.parser.Source

data class ParsedVoiceoverBookMetadata(
    val source: Source,
    val title: String,
    val authors: List<String>,
    val series: String?,
    val seriesIndex: Int?,
    val coverUrl: String,
    val relatedVoiceoverId: String,
)

data class ParsedVoiceover(
    val source: Source,
    val internalId: String,
    val readers: List<String>,
    val mediaItems: List<ParsedMediaItem>
)

data class ParsedMediaItem(
    val url: String,
    val title: String,
    val duration: Long,
)