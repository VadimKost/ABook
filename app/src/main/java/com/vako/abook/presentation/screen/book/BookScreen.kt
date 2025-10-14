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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vako.abook.player.AudioBookPlayer.Companion.rememberServiceAudioBookPlayer
import com.vako.abook.presentation.components.debugPlaceholder
import com.vako.domain.book.model.Author
import com.vako.abook.player.PlayerState
import com.vako.domain.player.model.Playlist

@Composable
fun BookScreen(
    onAction: (BookAction) -> Unit
) {
    val viewModel: BookViewModel = hiltViewModel()
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val audioBookPlayer = rememberServiceAudioBookPlayer()
    val playbackState = audioBookPlayer.state.collectAsStateWithLifecycle().value

    BookContent(
        playerState = playbackState,
        title = state.book.title,
        authors = state.book.authors,
        selectedNarratorName = state.book.voiceovers.map { it.readers }.joinToString(),
        coverUrl = state.book.cover,
        onSelectVoiceoverClick = {},
        onPlay = {
            if (playbackState is PlayerState.Ready){
                if (playbackState.playlist.mediaItems == state.selectedVoiceover?.mediaItems){
                    audioBookPlayer.mediaController?.prepare()
                    audioBookPlayer.mediaController?.play()
                }
            } else {
                val voiceover = state.selectedVoiceover
                val playlist = voiceover?.let {
                    Playlist(
                        name = state.book.title,
                        cover = state.book.cover,
                        mediaItems = it.mediaItems
                    )
                }
                playlist?.let {
                    audioBookPlayer.setPlaylist(it)
                }
            }
        },
        onPause = {
            audioBookPlayer.mediaController?.pause()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookContent(
    playerState: PlayerState,
    title: String,
    authors: List<Author>,
    selectedNarratorName: String,
    coverUrl: String,
    onSelectVoiceoverClick: () -> Unit,
    onPlay: () -> Unit = {},
    onPause: () -> Unit = {},
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
                            Text(
                                "Narrator: $selectedNarratorName",
                                style = MaterialTheme.typography.titleSmall
                            )
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
                        playerState = playerState,
                        onPlay = onPlay,
                        onPause = onPause
                    )
                }
            }
        }
    )
}

@Composable
fun PlayerControls(
    playerState: PlayerState,
    onPlay: () -> Unit,
    onPause: () -> Unit
) {
    if (playerState is PlayerState.Ready) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(32.dp)
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.FastRewind,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(32.dp)
                )
            }
            val playIcon =
                if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow

            FilledIconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = playIcon,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(32.dp)
                        .clickable {
                            if (playerState.isPlaying) {
                                onPause()
                            } else {
                                onPlay()
                            }
                        }
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(32.dp)
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(32.dp)
                )
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            FilledIconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(32.dp)
                        .clickable { onPlay() }
                )
            }
        }
    }
}

@Preview
@Composable
private fun BookPreview() {
    BookContent(
        playerState = PlayerState.Initializing,
        title = "Test Book",
        authors = listOf(Author(fullName = "Test Author")),
        selectedNarratorName = "Test Narrator",
        coverUrl = "",
        onSelectVoiceoverClick = {},
    )
}