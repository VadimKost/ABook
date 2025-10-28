package com.vako.domain.book.usecases

import com.vako.domain.book.BookRepository
import com.vako.domain.shared.Resource
import com.vako.domain.shared.ResourceError
import com.vako.domain.shared.executeUseCase
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(query: String) = executeUseCase(Dispatchers.IO) {
        if (query.isUrl()) {
            val book = bookRepository.getBookByUrl(query)
            if (book != null) {
                Resource.Success(listOf(book))
            } else {
                Resource.Error(
                    error = ResourceError.Custom("Book with url $query not found")
                )
            }
        } else {
            Resource.Error(
                error = ResourceError.Custom("Can`t process search string $query")
            )
        }
    }
}

fun String.isUrl(): Boolean {
    return startsWith("http")
}