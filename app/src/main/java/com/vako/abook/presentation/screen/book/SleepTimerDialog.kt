package com.vako.abook.presentation.screen.book

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlin.time.Duration.Companion.minutes

val PREDEFINED_SLEEP_TIMER_OPTIONS = listOf(
    10.minutes,
    20.minutes,
    30.minutes,
    45.minutes,
    60.minutes
)

@Composable
fun SleepTimerDialog(
    onDismissRequest: () -> Unit,
    onSelectTime: (Int) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        SleepTimerDialogContent(
            onSelectTime = onSelectTime,
            onClose = onDismissRequest,
        )
    }
}

@Composable
private fun SleepTimerDialogContent(
    onClose: () -> Unit,
    onSelectTime: (Int) -> Unit,
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
                text = "Set Timer",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .align(Alignment.Center)
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PREDEFINED_SLEEP_TIMER_OPTIONS.forEach { time ->
                SleepTimerPlate(
                    label = "${time.inWholeMinutes} min",
                    onClick = { onSelectTime(time.inWholeSeconds.toInt()) }
                )
            }
        }
        HorizontalDivider()
        Row(Modifier.fillMaxWidth()) {
            TextButton(
                onClick = onClose,
            ) {
                Text("Custom")
            }
            Spacer(Modifier.weight(1f))
            TextButton(
                onClick = onClose,
            ) {
                Text("Close")
            }
        }
    }
}

@Composable
private fun SleepTimerPlate(
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 3.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp)
        )
    }
}

@Preview
@Composable
private fun SleepTimerDialogPreview() {
    MaterialTheme {
        SleepTimerDialog(
            onDismissRequest = {},
            onSelectTime = {},
        )
    }
}