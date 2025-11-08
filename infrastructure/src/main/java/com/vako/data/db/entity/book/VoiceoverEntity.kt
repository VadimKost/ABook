package com.vako.data.db.entity.book

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "Voiceover",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = arrayOf("inAppId"),
            childColumns = arrayOf("bookId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VoiceoverEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "bookId")
    var bookId: String
)

