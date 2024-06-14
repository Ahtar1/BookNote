package com.example.booknote.presentation.util

sealed class Page(val route: String) {
    data object BooksPage: Page("books_page")
    data object NotesPage: Page("notes_page")
}