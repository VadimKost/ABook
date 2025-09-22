package com.vako.domain.usecases

import com.vako.domain.book.BookRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRandomBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke() = bookRepository.getRandomBooks()
}