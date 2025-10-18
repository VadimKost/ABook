package com.vako.abook.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun PlayerPlaybackProgress(
    currentPositionMs: Long,
    totalDurationMs: Long,
    onSeekTo: (Long) -> Unit,
) {
    var sliderPosition by remember { mutableFloatStateOf(0F) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(currentPositionMs, isDragging) {
        if (!isDragging) {
            sliderPosition = currentPositionMs.toFloat()
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
            valueRange = 0f..(totalDurationMs).toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatTime(sliderPosition.toLong()))
            Text(formatTime(totalDurationMs))
        }
    }
}

fun formatTime(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}