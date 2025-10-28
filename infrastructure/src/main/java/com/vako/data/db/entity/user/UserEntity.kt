package com.vako.data.db.entity.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "User")
data class UserEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val isCurrent: Boolean = false,
    val displayName: String,
)