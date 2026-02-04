package com.vako.data.db.entity.book

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Reader",
    indices = [Index(value = ["fullName"], unique = true)]
)
data class ReaderEntity(

    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "fullName")
    val fullName: String,
)
