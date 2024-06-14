package com.example.booknote.domain.model.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.booknote.domain.model.Book
import com.example.booknote.domain.model.Note

data class BookWithNotes(
    @Embedded val book: Book,
    @Relation(
        parentColumn = "id",
        entityColumn = "bookId"
    )
    val notes: List<Note>
)