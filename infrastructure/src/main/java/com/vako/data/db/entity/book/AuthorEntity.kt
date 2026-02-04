package com.vako.data.db.entity.book

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Author")
data class AuthorEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "fullName")
    val fullName: String
)
