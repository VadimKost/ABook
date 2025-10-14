package com.vako.domain.player.model

sealed class PlaybackEvent {
        data object Play : PlaybackEvent()
        data object Pause : PlaybackEvent()

        data object Next : PlaybackEvent()
        data object Previous : PlaybackEvent()

        data object Rewind : PlaybackEvent()
        data object FastForward : PlaybackEvent()

        data class SeekTo(val index: Int, val time: Long = 0) : PlaybackEvent()
    }