package com.example.booknote.presentation.books

import com.example.booknote.domain.model.Book

sealed class BooksEvent {
    data class DeleteBook(val book: Book): BooksEvent()
    data class AddBook(val book: Book): BooksEvent()
    data class GetBooks(val searchQuery: String): BooksEvent()
    object RestoreBook: BooksEvent()
    object AddBookButtonClicked: BooksEvent()

    object DismissDialog: BooksEvent()
}