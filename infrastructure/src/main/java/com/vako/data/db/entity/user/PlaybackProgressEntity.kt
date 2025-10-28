package com.vako.data.db.entity.user

import androidx.room.Entity

@Entity(
    tableName = "PlaybackProgress",
    primaryKeys = ["userId", "bookId", "voiceoverId"]
)
data class PlaybackProgressEntity(
    val userId: String,
    val bookId: String,
    val voiceoverId: String,
    val trackIndex: Int,
    val positionMs: Long,
)