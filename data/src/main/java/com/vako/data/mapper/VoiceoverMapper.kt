package com.vako.data.mapper

import com.vako.data.db.entity.book.ReaderEntity
import com.vako.data.db.entity.book.VoiceoverEntity
import com.vako.data.db.entity.book.detailed.VoiceoverWithDetails
import com.vako.data.mapper.base.ListEntityMapper
import com.vako.domain.book.model.Reader
import com.vako.domain.book.model.Voiceover
import java.util.UUID
import javax.inject.Inject

class VoiceoverMapper @Inject constructor(
    private val mediaItemMapper: MediaItemMapper,
) : ListEntityMapper<VoiceoverWithDetails, Voiceover> {

    override fun toDomain(entity: VoiceoverWithDetails): Voiceover = Voiceover(
        readers = entity.readers.map { Reader(it.fullName) },
        mediaItems = entity.mediaItems.map { mediaItemMapper.toDomain(it) }
    )

    override fun toEntity(domain: Voiceover): VoiceoverWithDetails {
        val voiceoverId = UUID.randomUUID().toString()

        return VoiceoverWithDetails(
            voiceover = VoiceoverEntity(
                id = voiceoverId,
                bookId = ""  // This will be set when adding to book
            ),
            readers = domain.readers.map {
                ReaderEntity(
                    id = UUID.randomUUID().toString(),
                    fullName = it.fullName
                )
            },
            mediaItems = domain.mediaItems.map {
                mediaItemMapper.toEntity(it).copy(voiceoverId = voiceoverId)
            }
        )
    }
}
