package com.vako.data.mapper.book

import com.vako.data.db.entity.book.MediaItemEntity
import com.vako.domain.shared.model.MediaItem

fun MediaItem.toEntity(voiceoverId: String) = MediaItemEntity(
    url = this.uri,
    title = this.title,
    durationS = this.durationS,
    voiceoverId = voiceoverId
)

fun MediaItemEntity.toDomain() = MediaItem(
    uri = this.url,
    title = this.title,
    durationS = this.durationS
)