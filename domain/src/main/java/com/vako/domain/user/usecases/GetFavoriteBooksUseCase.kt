package com.vako.domain.user.usecases

import com.vako.domain.book.BookRepository
import com.vako.domain.book.model.Book
import com.vako.domain.shared.Resource
import com.vako.domain.shared.executeUseCase
import com.vako.domain.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: Redo
class GetFavoriteBooksUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(): Resource<List<Book>> = executeUseCase(Dispatchers.IO) {
        val user = userRepository.getCurrentUser()
        val bookIds = user.favoriteBookIds
        val books = bookIds.map {
            async { bookRepository.getBookByInAppId(it) }
        }.awaitAll().filterNotNull()

        return@executeUseCase Resource.Success(books)
    }
}