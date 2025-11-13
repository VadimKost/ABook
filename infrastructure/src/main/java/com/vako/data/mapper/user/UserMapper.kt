package com.vako.data.mapper.user

import com.vako.data.db.entity.user.UserEntity
import com.vako.data.db.entity.user.detailed.UserWithDetails
import com.vako.domain.player.model.PlaybackProgress
import com.vako.domain.user.model.BookVoiceover
import com.vako.domain.user.model.User

fun UserWithDetails.toDomain(): User {
    val favoriteBookIds = this.favoriteBooks.map { it.bookId }.toSet()
    val preferredVoiceovers = this.preferredVoiceovers.associate { entity ->
        entity.bookId to entity.voiceoverId
    }.toMutableMap()

    val playbackProgress = this.playbackProgress.associate { entity ->
        BookVoiceover(
            bookId = entity.bookId,
            voiceoverId = entity.voiceoverId
        ) to PlaybackProgress(
            positionMs = entity.positionMs,
            trackIndex = entity.trackIndex
        )
    }.toMutableMap()

    return User(
        id = this.user.id,
        displayName = this.user.displayName,
        favoriteBookIds = favoriteBookIds,
        preferredVoiceovers = preferredVoiceovers,
        playbackProgress = playbackProgress
    )
}

fun UserEntity.toDomain() = User(
    id = this.id,
    displayName = this.displayName,
    favoriteBookIds = setOf(),
    preferredVoiceovers = mutableMapOf(),
    playbackProgress = mutableMapOf()
)