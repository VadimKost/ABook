package com.vako.domain.player

import com.vako.domain.player.usecases.PlaybackCommand
import com.vako.domain.player.model.PlayerState
import com.vako.domain.player.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingCommand

interface PlayerGateway {
    fun observePlayerState(): Flow<PlayerState>
    fun getCurrentPlayerState(): PlayerState
    fun play()
    fun pause()
    fun seekToNext()
    fun seekToPrevious()
    fun fastForward()
    fun rewind()
    fun seekTo(index: Int, time: Long)
    fun setPlaylist(playlist: Playlist): Boolean
    fun startSleepTimer(durationInSeconds: Int)
    fun cancelSleepTimer()
}
