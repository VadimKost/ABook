package com.vako.data.db.entity.book

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MediaItem")
data class MediaItemEntity(

    @PrimaryKey
    val url: String,

    @ColumnInfo(name = "voiceoverId")
    val voiceoverId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "duration")
    val durationS: Long
)
