package com.example.booknote.presentation.add_book

import com.example.booknote.domain.model.Book

sealed class AddBookEvent {
    data class GetBook(val bookId: Long) : AddBookEvent()
    data class SaveBook(val book: Book) : AddBookEvent()
}