package com.vako.domain.user

typealias BookId = String
typealias VoiceoverId = String

data class User(
    val id: String,
    val displayName: String,
    val favoriteBookIds: Set<String>,
    val preferredVoiceovers: MutableMap<BookId, VoiceoverId>
)
