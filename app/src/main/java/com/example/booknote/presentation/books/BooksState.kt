package com.example.booknote.presentation.books

import com.example.booknote.domain.model.Book
import com.example.booknote.domain.util.BooksSortOrder
import com.example.booknote.domain.util.SortOrder

data class BooksState(
    val books: List<Book> = emptyList(),
    val searchQuery: String ="",
    val order: SortOrder = BooksSortOrder.BookTitleAsc,
)
