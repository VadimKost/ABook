package com.vako.domain.user.model

import com.vako.domain.player.model.PlaybackProgress

typealias BookId = String
typealias VoiceoverId = String

data class User(
    val id: String,
    val displayName: String,
    val favoriteBookIds: Set<String>,
    val preferredVoiceovers: MutableMap<BookId, VoiceoverId>,
    val playbackProgress: MutableMap<BookVoiceover, PlaybackProgress>,
)

data class BookVoiceover(val bookId: BookId, val voiceoverId: VoiceoverId)