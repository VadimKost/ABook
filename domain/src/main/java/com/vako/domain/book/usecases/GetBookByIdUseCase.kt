package com.vako.domain.book.usecases

import com.vako.domain.book.BookRepository
import com.vako.domain.shared.Resource
import com.vako.domain.shared.ResourceError
import com.vako.domain.shared.executeUseCase
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetBookByIdUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(inAppId: String) = executeUseCase(Dispatchers.IO) {
        val book = bookRepository.getBookByInAppId(inAppId)
        if (book != null) {
            Resource.Success(book)
        } else Resource.Error(error = ResourceError.Custom("Book with id $inAppId not found"))

    }
}