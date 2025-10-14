package com.vako.abook.player

import android.os.Bundle
import androidx.media3.session.SessionCommand

// TODO: Improve this piece of hmmm code....
sealed class CustomPlaybackServiceCommand(val name: String) {
    companion object {
        val commands = listOf(
            StartSleepTimerCommand(),
            CancelSleepTimerCommand()
        )
    }
}

class StartSleepTimerCommand(name: String = "START_SLEEP_TIMER") :
    CustomPlaybackServiceCommand(name) {
    val timeToPlayKey = "timeToPlayKey"

    fun fromBundle(bundle: Bundle): Int {
        return bundle.getInt(timeToPlayKey)
    }

    fun toBundle(timeToPlay: Int): Bundle {
        return Bundle().apply {
            putInt(timeToPlayKey, timeToPlay)
        }
    }
}

class CancelSleepTimerCommand(name: String = "CANCEL_SLEEP_TIMER") :
    CustomPlaybackServiceCommand(name)

fun SessionCommand.toCustomPlaybackServiceCommand(): CustomPlaybackServiceCommand? =
    CustomPlaybackServiceCommand.commands.find { it.name == customAction }


data class SessionExtras(
    val timeToSleep: Int
)

const val TIME_TO_SLEEP_KEY = "TIME_TO_PLAY_KEY"

fun SessionExtras.toBundle(): Bundle {
    return Bundle().apply {
        putInt(TIME_TO_SLEEP_KEY, timeToSleep)
    }
}

fun Bundle.fromBundle(): SessionExtras {
    val timeToSleep = getInt(TIME_TO_SLEEP_KEY)
    return SessionExtras(timeToSleep)
}
//