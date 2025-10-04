package com.vako.data.db.entity.book.crossref

import androidx.room.Entity

@Entity(
    tableName = "VoiceoverReaderCrossRef",
    primaryKeys = ["voiceoverId", "readerId"],
)
data class VoiceoverReaderCrossRef(
    val voiceoverId: String,
    val readerId: String
)
