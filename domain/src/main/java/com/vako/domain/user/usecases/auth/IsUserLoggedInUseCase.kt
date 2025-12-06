package com.vako.domain.user.usecases.auth

import com.vako.domain.user.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsUserLoggedInUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Boolean {
        return userRepository.getCurrentUser() != null
    }
}