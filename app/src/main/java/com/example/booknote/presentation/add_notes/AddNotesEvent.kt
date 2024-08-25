package com.example.booknote.presentation.add_notes

import com.example.booknote.domain.model.Book
import com.example.booknote.domain.model.Note

sealed class AddNotesEvent {
    data class AddNote(val note: Note): AddNotesEvent()
    data class GetNote(val noteId: Long): AddNotesEvent()
}