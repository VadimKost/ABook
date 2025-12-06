package com.vako.domain.user.usecases.auth

import com.vako.domain.user.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignInViaGoogleUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(token: String) {
        val currentUser = userRepository.getCurrentUser()
        if (currentUser == null) {
            userRepository.signInViaGoogle(token)
        } else {
            userRepository.linkWithCredential(token)
        }
    }
}