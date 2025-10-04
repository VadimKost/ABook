package com.vako.data.mapper.book

import com.vako.data.db.entity.book.MediaItemEntity
import com.vako.data.db.entity.book.ReaderEntity
import com.vako.data.db.entity.book.VoiceoverEntity
import com.vako.data.db.entity.book.detailed.VoiceoverWithDetails
import com.vako.data.parser.model.ParsedVoiceover
import com.vako.domain.book.model.Voiceover
import java.util.UUID

fun ParsedVoiceover.toVoiceoverWithDetails(inAppId: String): VoiceoverWithDetails {
    val voiceoverId = UUID.randomUUID().toString()
    return VoiceoverWithDetails(
        voiceover = VoiceoverEntity(
            id = voiceoverId,
            bookId = inAppId
        ),
        readers = this.readers.map {
            ReaderEntity(
                id = UUID.randomUUID().toString(),
                fullName = it
            )
        },
        mediaItems = this.mediaItems.map { mi ->
            MediaItemEntity(
                url = mi.url,
                voiceoverId = voiceoverId,
                title = mi.title,
                duration = mi.duration
            )
        }
    )
}

fun VoiceoverWithDetails.toDomain(): Voiceover {
    return Voiceover(
        readers = this.readers.map { it.toDomain() },
        mediaItems = this.mediaItems.map { it.toDomain() }
    )
}