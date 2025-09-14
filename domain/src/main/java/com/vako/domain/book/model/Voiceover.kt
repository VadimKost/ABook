package com.vako.domain.book.model

import com.vako.domain.shared.model.MediaItem

data class Voiceover(
    val readers: List<Reader>,
    val mediaItems: List<MediaItem>,
)
