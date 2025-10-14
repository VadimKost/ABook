package com.vako.domain.player.model

import com.vako.domain.shared.model.MediaItem

data class Playlist(
    val name: String,
    val cover: String,
    val mediaItems: List<MediaItem>
)