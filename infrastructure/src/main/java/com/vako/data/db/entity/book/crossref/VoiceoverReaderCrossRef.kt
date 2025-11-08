package com.vako.data.db.entity.book.crossref

import androidx.room.Entity
import androidx.room.ForeignKey
import com.vako.data.db.entity.book.ReaderEntity
import com.vako.data.db.entity.book.VoiceoverEntity

@Entity(
    tableName = "VoiceoverReaderCrossRef",
    primaryKeys = ["voiceoverId", "readerId"],
    foreignKeys = [
        ForeignKey(
            entity = VoiceoverEntity::class,
            parentColumns = ["id"],
            childColumns = ["voiceoverId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ReaderEntity::class,
            parentColumns = ["id"],
            childColumns = ["readerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VoiceoverReaderCrossRef(
    val voiceoverId: String,
    val readerId: String
)
