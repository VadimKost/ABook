package com.vako.domain.user.usecases

import com.vako.domain.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveBookIsFavoriteUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke(bookId: String): Flow<Boolean> = flow {
        userRepository.observeCurrentUser().collect { user ->
            if (user == null) {
                emit(false)
            } else {
                emit(bookId in user.favoriteBookIds)
            }
        }
    }
}