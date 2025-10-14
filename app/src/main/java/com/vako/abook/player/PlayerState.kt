package com.vako.abook.player

import com.vako.domain.player.model.Playback
import com.vako.domain.player.model.Playlist

sealed class PlayerState {
    data object Initializing : PlayerState()
    data object Idle : PlayerState()
    data class Ready(
        val playback: Playback,
        val playlist: Playlist
    ) : PlayerState()
}