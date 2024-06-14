package com.example.booknote.presentation.books

import com.example.booknote.domain.model.Book

data class BooksState(
    val books: List<Book> = emptyList(),
    val searchQuery: String =""
)
