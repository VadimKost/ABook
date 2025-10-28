package com.vako.domain.user.usecases

import com.vako.domain.player.model.PlaybackProgress
import com.vako.domain.user.UserRepository
import com.vako.domain.user.model.BookVoiceover
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestorePlaybackProgressUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(bookId: String, voiceoverId: String): PlaybackProgress? {
        val user = userRepository.getCurrentUser()
        return user.playbackProgress[BookVoiceover(bookId, voiceoverId)]
    }
}
