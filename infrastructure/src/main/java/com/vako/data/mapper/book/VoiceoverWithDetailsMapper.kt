package com.vako.data.mapper.book

import com.vako.data.db.entity.book.ExternalVoiceoverEntity
import com.vako.data.db.entity.book.MediaItemEntity
import com.vako.data.db.entity.book.ReaderEntity
import com.vako.data.db.entity.book.VoiceoverEntity
import com.vako.data.db.entity.book.detailed.VoiceoverWithDetails
import com.vako.data.parser.model.ParsedVoiceover
import com.vako.data.mapper.util.toStableId
import com.vako.domain.book.model.Voiceover

fun ParsedVoiceover.toVoiceoverWithDetails(
    inAppId: String,
    voiceoverId: String
): VoiceoverWithDetails {
    return VoiceoverWithDetails(
        voiceover = VoiceoverEntity(
            id = voiceoverId,
            bookId = inAppId
        ),
        readers = this.readers.map {
            ReaderEntity(
                id = stableReaderId(it),
                fullName = it
            )
        },
        mediaItems = this.mediaItems.map { mi ->
            MediaItemEntity(
                url = mi.url,
                voiceoverId = voiceoverId,
                title = mi.title,
                durationS = mi.duration
            )
        },
        externalVoiceoverEntity = ExternalVoiceoverEntity(
            voiceoverId = voiceoverId,
            source = this.source,
            externalId = this.internalId
        )
    )
}


fun stableVoiceoverId(sourceName: String, externalVoiceoverId: String, bookId: String): String {
    return ("$bookId|$sourceName|$externalVoiceoverId").toStableId()
}

fun stableReaderId(readerFullName: String): String {
    return readerFullName.toStableId()
}

fun VoiceoverWithDetails.toDomain(): Voiceover {
    return Voiceover(
        id = this.voiceover.id,
        readers = this.readers.map { it.toDomain() },
        mediaItems = this.mediaItems.map { it.toDomain() }
    )
}