package com.vako.data.db.entity.user.detailed

import androidx.room.Embedded
import androidx.room.Relation
import com.vako.data.db.entity.user.FavoriteBookEntity
import com.vako.data.db.entity.user.PlaybackProgressEntity
import com.vako.data.db.entity.user.PreferredVoiceoverEntity
import com.vako.data.db.entity.user.UserEntity

data class UserWithDetails (
    @Embedded
    val user: UserEntity,

    @Relation(
        entity = FavoriteBookEntity::class,
        parentColumn = "id",
        entityColumn = "userId",
    )
    val favoriteBooks: List<FavoriteBookEntity>,

    @Relation(
        entity = PlaybackProgressEntity::class,
        parentColumn = "id",
        entityColumn = "userId",
    )
    val playbackProgress: List<PlaybackProgressEntity>,

    @Relation(
        entity = PreferredVoiceoverEntity::class,
        parentColumn = "id",
        entityColumn = "userId",
    )
    val preferredVoiceovers: List<PreferredVoiceoverEntity>
)