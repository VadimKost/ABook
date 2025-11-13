package com.vako.domain.user.usecases

import com.vako.domain.book.BookRepository
import com.vako.domain.book.model.Book
import com.vako.domain.shared.Resource
import com.vako.domain.shared.executeUseCase
import com.vako.domain.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

// TODO: Redo
class ObserveFavoriteBooksUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val bookRepository: BookRepository
) {
    operator fun invoke(): Flow<Resource<List<Book>>> = flow {
        userRepository.observeCurrentUser().collect { user ->
            val favoriteBookIds = user?.favoriteBookIds
            val favoriteBooks =
                favoriteBookIds?.mapNotNull { bookRepository.getBookByInAppId(it) } ?: emptyList()
            emit(Resource.Success(favoriteBooks))
        }
    }.flowOn(Dispatchers.IO)
}