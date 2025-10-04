package com.vako.data.mapper.book

import com.vako.data.db.entity.book.AuthorEntity
import com.vako.domain.book.model.Author

fun Author.toEntity(): AuthorEntity = AuthorEntity(
    fullName = this.fullName
)

fun AuthorEntity.toDomain(): Author = Author(
    fullName = this.fullName
)