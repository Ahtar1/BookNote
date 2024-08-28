package com.example.booknote.presentation.books

import com.example.booknote.domain.model.Book
import com.example.booknote.domain.util.NotesSortOrder

sealed class BooksEvent {
    data class DeleteBook(val books: List<Book>): BooksEvent()
    data class AddBook(val book: Book): BooksEvent()
    data class GetBooks(val searchQuery: String): BooksEvent()
    data class ChangeOrder(val order: NotesSortOrder): BooksEvent()
    object OrderButtonClicked: BooksEvent()
    object DismissBottomSheet: BooksEvent()
    object AddBookButtonClicked: BooksEvent()
    object DismissDialog: BooksEvent()
}