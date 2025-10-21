package com.vako.abook.presentation.screen.book

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vako.domain.book.model.Reader
import com.vako.domain.book.model.Voiceover
import com.vako.domain.shared.model.MediaItem

@Composable
fun VoiceoverSelectionDialog(
    voiceovers: List<Voiceover>,
    selectedVoiceover: Voiceover?,
    onDismissRequest: () -> Unit,
    onSelectVoiceover: (Voiceover) -> Unit,
) {
    val scrollState = rememberScrollState()
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        VoiceoverSelectionDialogContent(
            scrollState = scrollState,
            voiceovers = voiceovers,
            selectedVoiceover = selectedVoiceover,
            onClose = onDismissRequest,
            onSelectVoiceover = onSelectVoiceover
        )
    }
}

@Composable
private fun VoiceoverSelectionDialogContent(
    scrollState: ScrollState,
    voiceovers: List<Voiceover>,
    selectedVoiceover: Voiceover?,
    onClose: () -> Unit,
    onSelectVoiceover: (Voiceover) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 360.dp)
    ) {
        val backgroundColor = MaterialTheme.colorScheme.secondaryContainer
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
        ) {
            Text(
                text = "Select Voiceover",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .align(Alignment.Center)
            )
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            voiceovers.forEach {
                val isSelected = it == selectedVoiceover
                VoiceoverSelectionItem(
                    voiceover = it,
                    isSelected = isSelected,
                    onClick = { onSelectVoiceover(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
        }
        HorizontalDivider()
        TextButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Close")
        }
    }
}

@Composable
fun VoiceoverSelectionItem(
    voiceover: Voiceover,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    else
        Color.Transparent

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = voiceover.readers.joinToString(", ") { it.fullName },
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${voiceover.mediaItems.size} tracks, ${
                    formatTotalDuration(voiceover.mediaItems)
                }",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun formatTotalDuration(items: List<MediaItem>): String {
    val totalSeconds = items.sumOf { it.durationS }
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@Preview
@Composable
private fun VoiceoverSelectionDialogPreview() {
    MaterialTheme {
        VoiceoverSelectionDialog(
            onDismissRequest = {},
            voiceovers = listOf(
                Voiceover(
                    id = "1",
                    readers = listOf(
                        Reader("Vivien Lee")
                    ),
                    mediaItems = emptyList()
                ),
                Voiceover(
                    id = "2",
                    readers = listOf(
                        Reader("Frank Miller")
                    ),
                    mediaItems = emptyList()
                ),
            ),
            selectedVoiceover = Voiceover(
                id = "2",
                readers = listOf(
                    Reader("Frank Miller")
                ),
                mediaItems = emptyList()
            ),
            onSelectVoiceover = {}
        )
    }
}