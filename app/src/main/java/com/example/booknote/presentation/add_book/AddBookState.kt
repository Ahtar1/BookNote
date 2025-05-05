package com.example.booknote.presentation.add_book

import com.example.booknote.domain.model.Book

data class AddBookState(
    val bookId: Long = 0,
    val title: String = "",
    val author: String = "",
    val publisher: String = "",
    val language: String = "",
    val status: Book.BookStatus = Book.BookStatus.TO_READ,
)