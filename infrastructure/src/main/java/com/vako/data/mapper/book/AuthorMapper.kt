package com.vako.data.mapper.book

import com.vako.data.db.entity.book.AuthorEntity
import com.vako.data.mapper.util.toStableId
import com.vako.domain.book.model.Author

fun Author.toEntity(): AuthorEntity = AuthorEntity(
    id = stableAuthorId(this.fullName),
    fullName = this.fullName
)

fun AuthorEntity.toDomain(): Author = Author(
    fullName = this.fullName
)

fun stableAuthorId(authorFullName: String): String {
    return authorFullName.toStableId()
}