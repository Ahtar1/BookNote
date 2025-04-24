package com.example.booknote.presentation.add_notes

import com.example.booknote.domain.model.Note

sealed class AddNotesEvent {
    data class AddNote(val note: Note): AddNotesEvent()
    data class GetNote(val noteId: Long): AddNotesEvent()
    data class ChangeColor(val color: Long): AddNotesEvent()
    data class SaveTags(val tags: List<String>, val noteId: Long?): AddNotesEvent()
    data object ToggleColorPicker: AddNotesEvent()
}