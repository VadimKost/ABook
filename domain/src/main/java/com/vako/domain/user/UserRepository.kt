package com.vako.domain.user

import com.vako.domain.player.model.PlaybackProgress
import com.vako.domain.user.model.User

interface UserRepository {
    fun getCurrentUser(): User

    // Save playback progress for given book/voiceover for current user
    suspend fun savePlaybackProgress(bookId: String, voiceoverId: String, progress: PlaybackProgress)
}