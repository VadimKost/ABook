package com.vako.data.db.entity.user

import androidx.room.Entity

@Entity(
    tableName = "FavoriteBook",
    primaryKeys = ["userId", "bookId"]
)
data class FavoriteBookEntity(
    val userId: String,
    val bookId: String,
    val addedAt: Long = System.currentTimeMillis()
)

