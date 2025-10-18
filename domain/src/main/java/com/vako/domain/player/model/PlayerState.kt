package com.vako.domain.player.model

sealed class PlayerState {
    data object Initializing : PlayerState()
    data object Idle : PlayerState()
    data class Ready(
        val isPlaying: Boolean,
        val playlist: Playlist,
        val currentTrackIndex: Int,
        val currentPosition: Long,
        val sleepTimerState: SleepTimerState
    ) : PlayerState()
}
//May create voiceoverPLaybackState without isSelectedVoiceoverActive
/*data class VoiceoverPlaybackState(
    val playlist: Playlist = Playlist(
        name = "",
        cover = "",
        mediaItems = listOf()
    ),
    val trackIndex: Int = 0,
    val positionMs: Long = 0,
    val isPlaying: Boolean = false,
    val sleepTimer: SleepTimerState = SleepTimerState(
        isRunning = false,
        timeRemainingSeconds = 0
    ),
    val isSelectedVoiceoverActive: Boolean = false
) {
    fun hasNext(): Boolean{

    }
}*/
