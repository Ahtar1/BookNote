package com.example.booknote.presentation.util

sealed class Page(val route: String) {
    data object BooksPage: Page("books_page")
    data object NotesPage: Page("notes_page")
    data object AddNotePage: Page("add_note_page")
    data object AddAudioPage: Page("add_audio_page")
    data object CalendarPage: Page("calendar_page")
    data object DrawNotePage: Page("draw_note_page")
    data object AddBookPage: Page("add_book_page")
    data object BookDetailsPage: Page("book_details_page")
}