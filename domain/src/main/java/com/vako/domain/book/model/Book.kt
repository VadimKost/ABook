package com.vako.domain.book.model

data class Book(
    val inAppId: String,
    val title: String,
    val cover: String,
    val authors: List<Author>,
    val voiceovers: List<Voiceover>,
    val series: Series? = null
)