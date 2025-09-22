package com.vako.data.mapper

import com.vako.data.db.entity.book.MediaItemEntity
import com.vako.data.mapper.base.ListEntityMapper
import com.vako.domain.shared.model.MediaItem
import javax.inject.Inject

class MediaItemMapper @Inject constructor() : ListEntityMapper<MediaItemEntity, MediaItem> {
    override fun toDomain(entity: MediaItemEntity): MediaItem = MediaItem(
        uri = entity.url,
        title = entity.title,
        duration = entity.duration
    )

    override fun toEntity(domain: MediaItem): MediaItemEntity = MediaItemEntity(
        url = domain.uri,
        title = domain.title,
        duration = domain.duration,
        voiceoverId = ""  // This will be set when adding to voiceover
    )
}
