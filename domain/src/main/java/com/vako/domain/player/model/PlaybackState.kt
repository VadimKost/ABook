package com.vako.domain.player.model

sealed class PlaybackState {
    data object Initializing : PlaybackState()
    data object Idle : PlaybackState()
    data class Ready(
        val timeToSleep: Int,
        val isPlaying: Boolean,
        val playlist: Playlist,
        val currentTrackIndex: Int,
        val currentPosition: Long,
    ) : PlaybackState()
}