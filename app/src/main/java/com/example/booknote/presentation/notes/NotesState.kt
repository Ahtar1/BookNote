package com.example.booknote.presentation.notes

import com.example.booknote.domain.model.Note

class NotesState(
    val notes: List<Note> = emptyList(),
    val searchQuery: String = ""
)