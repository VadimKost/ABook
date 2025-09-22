package com.vako.data.db.entity.book.detailed

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.vako.data.db.entity.book.MediaItemEntity
import com.vako.data.db.entity.book.ReaderEntity
import com.vako.data.db.entity.book.VoiceoverEntity
import com.vako.data.db.entity.book.crossref.VoiceoverReaderCrossRef

// Voiceover with its readers and media items
data class VoiceoverWithDetails(
    @Embedded val voiceover: VoiceoverEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = VoiceoverReaderCrossRef::class,
            parentColumn = "voiceoverId",
            entityColumn = "readerId"
        )
    )
    val readers: List<ReaderEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "voiceoverId"
    )
    val mediaItems: List<MediaItemEntity>
)
