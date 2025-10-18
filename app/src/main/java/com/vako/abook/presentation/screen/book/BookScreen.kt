package com.vako.abook.presentation.screen.book

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vako.abook.presentation.components.debugPlaceholder
import com.vako.domain.book.model.Author
import com.vako.domain.book.model.Voiceover
import com.vako.domain.player.usecases.PlaybackCommand
import com.vako.domain.player.model.PlayerState
import com.vako.domain.player.model.Playlist

@Composable
fun BookScreen(
    onAction: (BookAction) -> Unit
) {
    val viewModel: BookViewModel = hiltViewModel()
    val state = viewModel.state.collectAsStateWithLifecycle().value

    BookContent(
        voiceoverPlaybackState = state.playbackState,
        title = state.book.title,
        authors = state.book.authors,
        selectedVoiceover = state.selectedVoiceover,
        coverUrl = state.book.cover,
        onSelectVoiceoverClick = {},
        onPlaybackCommand = { command ->
            viewModel.onEvent(BookEvent.HandlePlaybackCommand(command))
        },
        voiceovers = state.book.voiceovers
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookContent(
    title: String,
    authors: List<Author>,
    coverUrl: String,
    voiceovers: List<Voiceover>,
    selectedVoiceover: Voiceover?,
    voiceoverPlaybackState: VoiceoverPlaybackState,
    onSelectVoiceoverClick: () -> Unit,
    onPlaybackCommand: (PlaybackCommand) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(title)
                }
            )
        },
        content = { it ->
            Column(
                Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1.6f),
                    placeholder = debugPlaceholder()
                )
                Column(
                    Modifier
                        .weight(1f)
                        .padding(24.dp)
                ) {
                    Row(Modifier.fillMaxWidth()) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Author: ${authors.joinToString { it.fullName }}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            val readers = selectedVoiceover?.readers
                            if (readers != null) {
                                Text(
                                    "Narrator: ${readers.joinToString()}",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(24.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(24.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(24.dp)
                        )
                    }
                    Box(Modifier.weight(1f))
                    PlayerControls(
                        voiceoverPlaybackState = voiceoverPlaybackState,
                        onPlaybackCommand = onPlaybackCommand,
                        onInitPlayback = {
                            val mediaItems = selectedVoiceover?.mediaItems
                            mediaItems?.let {
                                val playlist = Playlist(
                                    name = title,
                                    cover = coverUrl,
                                    mediaItems = it
                                )
                                onPlaybackCommand(PlaybackCommand.SetPlaylist(playlist))
                                onPlaybackCommand(PlaybackCommand.Play)
                            }


                        }
                    )
                }
            }
        }
    )
}

@Composable
fun PlayerPlaybackProgress(
    currentPosition: Long,
    totalDuration: Long,
    onSeekTo: (Long) -> Unit,
) {
    var sliderPosition by remember { mutableFloatStateOf(0F) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(currentPosition, isDragging) {
        if (!isDragging) {
            sliderPosition = currentPosition.toFloat()
        }
    }

    Column {
        Slider(
            value = sliderPosition,
            onValueChange = {
                isDragging = true
                sliderPosition = it
            },
            onValueChangeFinished = {
                isDragging = false
                val seekTime = sliderPosition.toLong()
                onSeekTo(seekTime)
            },
            valueRange = 0f..(totalDuration * 1000).toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatTime(sliderPosition.toLong()))
            Text(formatTime(totalDuration * 1000))
        }
    }
}

private fun formatTime(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:$seconds"
}

@Composable
fun PlayerControls(
    voiceoverPlaybackState: VoiceoverPlaybackState,
    onPlaybackCommand: (PlaybackCommand) -> Unit,
    onInitPlayback: () -> Unit
) {
    if (voiceoverPlaybackState.isSelectedVoiceoverActive) {
        Column(Modifier.fillMaxWidth()) {
            val currentMediaItemIndex = voiceoverPlaybackState.trackIndex
            val currentMediaItem =
                voiceoverPlaybackState.playlist.mediaItems[currentMediaItemIndex]
            val totalDuration = currentMediaItem.duration
            PlayerPlaybackProgress(
                currentPosition = voiceoverPlaybackState.positionMs,
                totalDuration = totalDuration,
                onSeekTo = { position ->
                    onPlaybackCommand(
                        PlaybackCommand.SeekTo(
                            currentMediaItemIndex,
                            position
                        )
                    )
                }
            )
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
private fun BookPreview() {
    BookContent(
        title = "Test Book",
        authors = listOf(Author(fullName = "Test Author")),
        coverUrl = "",
        onSelectVoiceoverClick = {},
        voiceoverPlaybackState = VoiceoverPlaybackState(),
        onPlaybackCommand = {},
        voiceovers = listOf(),
        selectedVoiceover = null,
    )
}