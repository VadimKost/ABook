package com.vako.data.db.entity.book

import androidx.room.Entity
import com.vako.data.parser.Source
//TODO think about external ids
@Entity(
    tableName = "ExternalBookEntityId",
    primaryKeys = ["inAppBookId", "source"]
)
data class ExternalBookId(
    val inAppBookId: String,
    val source: Source, // TODO: Remove bof not db class
    val externalId: String
)