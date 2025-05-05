package com.example.booknote.presentation.book_details

import com.example.booknote.domain.model.Book

sealed class BookDetailsEvent {
    data class GetBookDetails(val bookId: Long): BookDetailsEvent()
    data class UpdateBook(val book: Book): BookDetailsEvent()
}