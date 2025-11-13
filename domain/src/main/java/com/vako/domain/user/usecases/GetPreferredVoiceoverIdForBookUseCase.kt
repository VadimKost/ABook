package com.vako.domain.user.usecases

import com.vako.domain.user.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPreferredVoiceoverIdForBookUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(bookId: String): String? {
        val user = userRepository.getCurrentUser()
        return user.preferredVoiceovers[bookId]
    }
}