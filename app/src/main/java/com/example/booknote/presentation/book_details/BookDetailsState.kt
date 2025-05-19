package com.example.booknote.presentation.book_details

import com.example.booknote.domain.model.Book

data class BookDetailsState(
    val bookId: Long = 0,
    val title: String = "",
    val author: String = "",
    val publisher: String = "",
    val language: String = "",
    val status: Book.BookStatus = Book.BookStatus.TO_READ,
    val bookImageFilePath: String = "",
)