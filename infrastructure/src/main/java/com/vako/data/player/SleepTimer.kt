package com.vako.data.player

import com.vako.domain.player.model.SleepTimerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SleepTimer(
    val coroutineScope: CoroutineScope,
    val onSleep: () -> Unit
) {
    private var sleepTimerJob: Job? = null
    val state = MutableStateFlow(
        SleepTimerState(
            isRunning = false,
            timeRemainingSeconds = 0
        )
    )

    fun setSleepTimer(durationSeconds: Int) {
        cancelSleepTimer()
        sleepTimerJob = coroutineScope.launch {
            state.value =
                SleepTimerState(isRunning = true, timeRemainingSeconds = durationSeconds)
            var remaining = durationSeconds
            while (remaining > 0) {
                delay(1000)
                remaining -= 1
                state.value =
                    state.value.copy(timeRemainingSeconds = remaining)
            }
            onSleep()
            state.value = SleepTimerState(isRunning = false, timeRemainingSeconds = 0)
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        state.value = SleepTimerState(isRunning = false, timeRemainingSeconds = 0)
    }
}