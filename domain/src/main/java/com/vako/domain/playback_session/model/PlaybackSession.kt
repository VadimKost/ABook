package com.vako.domain.playback_session.model

data class PlaybackSession(
    val id: String,
    val bookId: String,
    val voiceoverId: String,
    val currentTrackIndex: Int,
    val currentPosition: Long,
    val timeStamp: Long
)