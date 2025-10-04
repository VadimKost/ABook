package com.vako.data.db.entity.book

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

// TODO: Replace inAppId: String to UUID(room support)
@Entity(
    tableName = "Book",
    indices = [Index(value = ["title"])]
)
data class BookEntity(

    @PrimaryKey
    @ColumnInfo(name = "inAppId")
    val inAppId: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "cover")
    val cover: String,

    @Embedded
    val series: SeriesEntity?,

    val createdAt: Long = System.currentTimeMillis(),

    val modifiedAt: Long = System.currentTimeMillis()
)
