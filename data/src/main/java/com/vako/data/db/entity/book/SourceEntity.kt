package com.vako.data.db.entity.book

import androidx.room.PrimaryKey

data class SourceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = -1,
    val referToId: String,
    val sourceName: String,
    val sourceId: String,
)
