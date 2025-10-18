package com.vako.abook.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vako.abook.presentation.screen.book.VoiceoverPlaybackState
import com.vako.domain.player.model.Playlist
import com.vako.domain.player.usecases.PlaybackCommand
import com.vako.domain.shared.model.MediaItem

// TODO: Make it clean(no ext dep)
@Composable
fun PlayerControls(
    voiceoverPlaybackState: VoiceoverPlaybackState,
    showPlaybackProgress: Boolean = true,
    onPlaybackCommand: (PlaybackCommand) -> Unit,
    onInitPlayback: () -> Unit
) {
    if (voiceoverPlaybackState.isSelectedVoiceoverActive) {
        Column(Modifier.fillMaxWidth()) {
            if (showPlaybackProgress) {
                val currentMediaItemIndex = voiceoverPlaybackState.trackIndex
                val currentMediaItem =
                    voiceoverPlaybackState.playlist.mediaItems[currentMediaItemIndex]
                val totalDurationMs = currentMediaItem.durationS * 1000
                PlayerPlaybackProgress(
                    currentPositionMs = voiceoverPlaybackState.positionMs,
                    totalDurationMs = totalDurationMs,
                    onSeekTo = { position ->
                        onPlaybackCommand(
                            PlaybackCommand.SeekTo(
                                currentMediaItemIndex,
                                position
                            )
                        )
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { onPlaybackCommand(PlaybackCommand.Previous) },
                    enabled = voiceoverPlaybackState.hasPrevious()
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(32.dp)
                    )
                }
                IconButton(onClick = { onPlaybackCommand(PlaybackCommand.Rewind) }) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(32.dp)
                    )
                }
                val playIcon =
                    if (voiceoverPlaybackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow

                FilledIconButton(
                    onClick = {
                        if (voiceoverPlaybackState.isPlaying) {
                            onPlaybackCommand(PlaybackCommand.Pause)
                        } else {
                            onPlaybackCommand(PlaybackCommand.Play)
                        }
                    }
                ) {
                    Icon(
                        imageVector = playIcon,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(32.dp)
                    )
                }
                IconButton(onClick = { onPlaybackCommand(PlaybackCommand.FastForward) }) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(32.dp)
                    )
                }
                IconButton(
                    onClick = { onPlaybackCommand(PlaybackCommand.Next) },
                    enabled = voiceoverPlaybackState.hasNext()
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(32.dp)
                    )
                }
            }
        }

    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            FilledIconButton(
                onClick = onInitPlayback
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(32.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PlayerControlsDefaultPreview() {
    MaterialTheme {
        PlayerControls(
            showPlaybackProgress = true,
            voiceoverPlaybackState = VoiceoverPlaybackState(
                isPlaying = false,
                positionMs = 60000,
                trackIndex = 0,
                playlist = Playlist(
                    name = "Sample Playlist",
                    cover = "null",
                    mediaItems = listOf(
                        MediaItem(
                            uri = "1",
                            title = "Sample Media Item",
                            durationS = 300
                        )
                    )
                ),
                isSelectedVoiceoverActive = true
            ),
            onPlaybackCommand = {},
            onInitPlayback = {}
        )
    }
}

@Preview
@Composable
private fun PlayerControlsNoProgressPreview() {
    MaterialTheme {
        PlayerControls(
            showPlaybackProgress = false,
            voiceoverPlaybackState = VoiceoverPlaybackState(
                isPlaying = false,
                positionMs = 60000,
                trackIndex = 0,
                playlist = Playlist(
                    name = "Sample Playlist",
                    cover = "null",
                    mediaItems = listOf(
                        MediaItem(
                            uri = "1",
                            title = "Sample Media Item",
                            durationS = 300
                        )
                    )
                ),
                isSelectedVoiceoverActive = true
            ),
            onPlaybackCommand = {},
            onInitPlayback = {}
        )
    }
}