package com.vako.data.repository.policy

import com.vako.data.db.entity.book.detailed.BookWithDetails
import java.util.concurrent.TimeUnit

class BookDetailingUpdatePolicy {
    companion object {
        fun shouldUpdate(detailedBook: BookWithDetails): Boolean {
            val createdAt = detailedBook.book.createdAt
            val modifiedAt = detailedBook.book.modifiedAt
            val deltaDays = TimeUnit.MILLISECONDS.toDays(modifiedAt - createdAt)

            if (createdAt == modifiedAt) return true
            return if (deltaDays >= 1) return true else false
        }
    }

}