package com.vako.data.mapper

import com.vako.data.db.entity.book.AuthorEntity
import com.vako.data.mapper.base.ListEntityMapper
import com.vako.domain.book.model.Author
import javax.inject.Inject

class AuthorMapper @Inject constructor() : ListEntityMapper<AuthorEntity, Author> {
    override fun toDomain(entity: AuthorEntity): Author = Author(
        fullName = entity.fullName
    )

    override fun toEntity(domain: Author): AuthorEntity = AuthorEntity(
        fullName = domain.fullName
    )
}
