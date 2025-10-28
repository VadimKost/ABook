package com.vako.domain.player.model

sealed class PlayerState {
    data object Initializing : PlayerState()
    data object Idle : PlayerState()
    data class Ready(
        val isPlaying: Boolean,
        val playlist: Playlist,
        val playbackProgress: PlaybackProgress,
        val sleepTimerState: SleepTimerState
    ) : PlayerState()
}