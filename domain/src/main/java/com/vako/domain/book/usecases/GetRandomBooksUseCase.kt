package com.vako.domain.book.usecases

import com.vako.domain.book.BookRepository
import com.vako.domain.shared.Resource
import com.vako.domain.shared.executeUseCase
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRandomBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke() = executeUseCase(Dispatchers.IO){
        val randomBooks = bookRepository.getRandomBooks()
        Resource.Success(randomBooks)
    }
}