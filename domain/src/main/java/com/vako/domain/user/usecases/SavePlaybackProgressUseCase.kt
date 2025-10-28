package com.vako.domain.user.usecases

import com.vako.domain.player.model.PlaybackProgress
import com.vako.domain.user.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavePlaybackProgressUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(bookId: String, voiceoverId: String, progress: PlaybackProgress) {
        userRepository.savePlaybackProgress(bookId, voiceoverId, progress)
    }
}
