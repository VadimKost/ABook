package com.vako.abook.player.features

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SleepTimerController(
    private val scope: CoroutineScope
) {
    private val _timeToSleep = MutableStateFlow(-1)
    val timeToSleep: StateFlow<Int> = _timeToSleep

    private var timerJob: Job? = null

    fun startTimer(durationSeconds: Int, onFinish: () -> Unit) {
        cancelTimer()

        _timeToSleep.value = durationSeconds
        timerJob = scope.launch {
            while (_timeToSleep.value > 0) {
                delay(1000)
                _timeToSleep.update { it - 1 }
            }
            onFinish()
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        _timeToSleep.value = -1
    }
}