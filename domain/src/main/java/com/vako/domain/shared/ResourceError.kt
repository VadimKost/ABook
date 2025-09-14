package com.vako.domain.shared

sealed interface ResourceError {
    data class Custom(val message: String) : ResourceError
}
