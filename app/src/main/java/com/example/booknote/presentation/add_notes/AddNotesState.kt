package com.example.booknote.presentation.add_notes

import com.example.booknote.domain.model.Note

data class AddNotesState(
    val note: Note = Note(
        id = 0,
        bookId = 0,
        noteTitle = "",
        page = 0,
        dateCreated = "",
        favorite = false
    ),
)