package com.vako.data.db.entity.book

import androidx.room.Entity
import androidx.room.ForeignKey
import com.vako.data.parser.Source
@Entity(
    tableName = "ExternalBookEntityId",
    primaryKeys = ["voiceoverId", "source"],
    foreignKeys = [
        ForeignKey(
            entity = VoiceoverEntity::class,
            parentColumns = ["id"],
            childColumns = ["voiceoverId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class ExternalVoiceoverEntity(
    val voiceoverId: String,
    val source: Source,
    val externalId: String
)