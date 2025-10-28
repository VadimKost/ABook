package com.vako.domain.player.model

import com.vako.domain.shared.model.MediaItem

sealed class Playlist(
    val name: String,
    val cover: String,
    val mediaItems: List<MediaItem>
)

class AudioBookVoiceoverPlaylist(
    name: String,
    cover: String,
    mediaItems: List<MediaItem>,
    val bookId: String,
    val voiceoverId: String
): Playlist(
    name = name,
    cover = cover,
    mediaItems = mediaItems
)