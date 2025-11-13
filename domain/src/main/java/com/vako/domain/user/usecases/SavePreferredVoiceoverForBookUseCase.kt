package com.vako.domain.user.usecases

import com.vako.domain.user.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavePreferredVoiceoverForBookUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(bookId: String, voiceoverId: String) {
        val user = userRepository.getCurrentUser()

        user.preferredVoiceovers[bookId]?.let { existingVoiceoverId ->
            userRepository.removePreferredVoiceover(
                userId = user.id,
                bookId = bookId,
                voiceoverId = existingVoiceoverId
            )
        }

        userRepository.savePreferredVoiceover(
            userId = user.id,
            bookId = bookId,
            voiceoverId = voiceoverId
        )
    }
}

