package com.vako.data.db.entity.user

import androidx.room.Entity

@Entity(
    tableName = "PreferredVoiceover",
    primaryKeys = ["userId", "bookId"]
)
data class PreferredVoiceoverEntity(
    val userId: String,
    val bookId: String,
    val voiceoverId: String,
    val selectedAt: Long = System.currentTimeMillis()
)

