package com.example.booknote.presentation.books

import com.example.booknote.domain.model.Book
import com.example.booknote.domain.util.NotesSortOrder

data class BooksState(
    val books: List<Book> = emptyList(),
    val searchQuery: String ="",
    val order: NotesSortOrder = NotesSortOrder.DateCreatedAsc,
)
