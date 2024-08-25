package com.example.booknote.presentation.util

sealed class Page(val route: String) {
    data object BooksPage: Page("books_page")
    data object NotesPage: Page("notes_page")
    data object AddNotePage: Page("add_note_page")
    data object AddAudioPage: Page("add_audio_page")
    data object AddImagePage: Page("add_image_page")
}