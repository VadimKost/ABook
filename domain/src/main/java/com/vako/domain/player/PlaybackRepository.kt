package com.vako.domain.player

import com.vako.domain.player.model.Playback


interface PlaybackRepository {
    fun getCurrentPlaybackState(): Playback
}