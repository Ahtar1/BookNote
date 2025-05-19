package com.example.booknote.presentation.focus

import com.example.booknote.domain.model.Book

data class FocusState(
    val elapsedSeconds: Long = 0L,
    val isRunning: Boolean = false,
    val selectedBook: Book = Book(
        id = -1,
        title = "",
        author = "",
        publisher = "",
        language = "",
        status = Book.BookStatus.TO_READ,
        bookImagePath = "",
    ),
    val books: List<Book> = emptyList(),
)