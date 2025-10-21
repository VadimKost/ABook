package com.vako.abook.presentation.screen.book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vako.abook.presentation.components.PlayerControls
import com.vako.abook.presentation.components.debugPlaceholder
import com.vako.abook.presentation.components.formatTime
import com.vako.domain.book.model.Author
import com.vako.domain.book.model.Voiceover
import com.vako.domain.player.model.Playlist
import com.vako.domain.player.model.SleepTimerState
import com.vako.domain.player.usecases.PlaybackCommand

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
        onSelectVoiceoverClick = {
            viewModel.onEvent(
                BookEvent.VoiceoverSelected(it)
            )
        },
        onPlaybackCommand = { command ->
            viewModel.onEvent(BookEvent.HandlePlaybackCommand(command))
        },
        voiceovers = state.book.voiceovers,
        showVoiceoverSelectionDialog = state.showVoiceoverSelectionDialog,
        showSleepTimerDialog = state.showSleepTimerDialog,
        onShowVoiceoverSelectionDialog = { show ->
            viewModel.onEvent(
                BookEvent.ShowVoiceoverSelectionDialog(show)
            )
        },
        onShowSleepTimerDialog = { show ->
            viewModel.onEvent(
                BookEvent.ShowSleepTimerDialog(show)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookContent(
    showVoiceoverSelectionDialog: Boolean,
    showSleepTimerDialog: Boolean,
    title: String,
    authors: List<Author>,
    coverUrl: String,
    voiceovers: List<Voiceover>,
    selectedVoiceover: Voiceover?,
    voiceoverPlaybackState: VoiceoverPlaybackState,
    onSelectVoiceoverClick: (Voiceover) -> Unit,
    onPlaybackCommand: (PlaybackCommand) -> Unit,
    onShowVoiceoverSelectionDialog: (Boolean) -> Unit,
    onShowSleepTimerDialog: (Boolean) -> Unit
) {
    if (showVoiceoverSelectionDialog) {
        VoiceoverSelectionDialog(
            voiceovers = voiceovers,
            selectedVoiceover = selectedVoiceover,
            onDismissRequest = { onShowVoiceoverSelectionDialog(false) },
            onSelectVoiceover = onSelectVoiceoverClick
        )
    }
    
    if (showSleepTimerDialog){
        SleepTimerDialog(
            onDismissRequest = { onShowSleepTimerDialog(false) },
            onSelectTime = { seconds ->
                onPlaybackCommand(PlaybackCommand.SetSleepTimer(seconds))
            }
        )
    }
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
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1.6f)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(coverUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxHeight(),
                        placeholder = debugPlaceholder()
                    )
                    if (voiceoverPlaybackState.sleepTimer.isRunning) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .clip(RoundedCornerShape(12))
                                .background(Color.White.copy(alpha = 0.2f)),
                            text = formatTime(voiceoverPlaybackState.sleepTimer.timeRemainingSeconds * 1000L),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        FilledTonalIconButton(
                            onClick = {onShowVoiceoverSelectionDialog(true)}
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "",
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        FilledTonalIconButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "",
                            )
                        }

                    }
                }

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
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(24.dp)
                                .clickable { onShowSleepTimerDialog(true) }
                        )
                        Icon(
                            imageVector = Icons.Default.Download,
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

@Preview
@Composable
private fun BookPreview() {
    MaterialTheme {
        BookContent(
            title = "Test Book",
            authors = listOf(Author(fullName = "Test Author")),
            coverUrl = "",
            onSelectVoiceoverClick = {},
            voiceoverPlaybackState = VoiceoverPlaybackState(
                sleepTimer = SleepTimerState(isRunning = true, timeRemainingSeconds = 1250)
            ),
            onPlaybackCommand = {},
            voiceovers = listOf(),
            selectedVoiceover = null,
            showVoiceoverSelectionDialog = false,
            showSleepTimerDialog = false,
            onShowVoiceoverSelectionDialog = {},
            onShowSleepTimerDialog = {},
        )
    }
}