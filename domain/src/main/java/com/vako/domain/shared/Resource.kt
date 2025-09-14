package com.vako.domain.shared

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

sealed interface Resource<T> {
    data class Success<T>(val data: T) : Resource<T>
    data class Error<T>(val error: ResourceError) : Resource<T>
}

suspend fun <T> executeUseCase(
    dispatcher: CoroutineDispatcher,
    action: suspend () -> Resource<T>
): Resource<T> {
    return withContext(dispatcher) {
        try {
            action()
        } catch (e: Exception) {
            print(e.printStackTrace())
            Resource.Error(ResourceError.Custom(message = e.message ?: ""))
        }
    }
}