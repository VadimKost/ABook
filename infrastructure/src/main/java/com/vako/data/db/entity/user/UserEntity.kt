package com.vako.data.db.entity.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val isCurrent: Boolean = false,
    val displayName: String,
)