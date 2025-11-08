package com.vako.domain.user

import com.vako.domain.player.model.PlaybackProgress
import com.vako.domain.user.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getCurrentUser(): User
    fun observeCurrentUser(): Flow<User?>
    suspend fun savePlaybackProgress(
        bookId: String,
        voiceoverId: String,
        progress: PlaybackProgress
    )

    suspend fun toggleIsFavoriteBook(bookId: String): Boolean
}