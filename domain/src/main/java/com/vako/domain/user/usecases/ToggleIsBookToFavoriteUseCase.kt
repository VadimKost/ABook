package com.vako.domain.user.usecases

import com.vako.domain.shared.Resource
import com.vako.domain.user.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToggleIsBookToFavoriteUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(bookId: String): Resource<Boolean> {
        return Resource.Success(userRepository.toggleIsFavoriteBook(bookId))
    }
}
