package com.example.booknote.presentation.notes

import com.example.booknote.domain.model.Book
import com.example.booknote.domain.model.Note
import com.example.booknote.domain.util.NotesSortOrder
import com.example.booknote.domain.util.SortOrder
import java.io.File

data class NotesState(
    val notes: List<Note> = emptyList(),
    val audios: List<File> = emptyList(),
    val searchQuery: String = "",
    val order: SortOrder = NotesSortOrder.DateCreatedDesc,
    val tags : List<String> = emptyList(),
    val book: Book =  Book(
        id= 0,
        title = "",
        author = "",
        publisher = "",
        language = "",
        status = Book.BookStatus.TO_READ,
        bookImagePath = "",
    )
)