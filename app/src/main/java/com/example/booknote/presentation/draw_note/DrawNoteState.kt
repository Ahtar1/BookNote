package com.example.booknote.presentation.draw_note

import com.example.booknote.domain.model.Note

data class DrawNoteState(
    val note: Note = Note(
        id = 0,
        bookId = 0,
        noteTitle = "",
        imageFilePath = "",
        page = 0,
        dateCreated = "",
        favorite = false,
        isDrawn = true
    ),
)