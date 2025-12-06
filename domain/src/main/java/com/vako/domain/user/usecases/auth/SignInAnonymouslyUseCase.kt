package com.vako.domain.user.usecases.auth

import com.vako.domain.user.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignInAnonymouslyUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        if (userRepository.getCurrentUser() == null){
            userRepository.signInAnonymously()
        }
    }
}