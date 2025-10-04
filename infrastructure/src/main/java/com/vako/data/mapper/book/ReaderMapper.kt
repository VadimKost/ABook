package com.vako.data.mapper.book

import com.vako.data.db.entity.book.ReaderEntity
import com.vako.domain.book.model.Reader

fun ReaderEntity.toDomain(): Reader {
    return Reader(
        fullName = this.fullName
    )
}