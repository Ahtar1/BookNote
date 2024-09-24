package com.example.booknote.presentation.notes

import android.content.Context
import com.example.booknote.domain.model.Note
import com.example.booknote.domain.util.NotesSortOrder
import com.example.booknote.domain.util.SortOrder

sealed class NotesEvent {
    data class DeleteNotes(val notes: List<Note>): NotesEvent()
    data class AddNote(val note: Note): NotesEvent()
    data class GetNotes(val bookId: String, val searchQuery: String, val notesSortOrder: SortOrder = NotesSortOrder.DateCreatedAsc): NotesEvent()
    data class GetAudios(val context: Context, val bookId: String): NotesEvent()
    data class InfoButtonClicked(val bookId: Long): NotesEvent()
    data class ChangeOrder(val order: SortOrder): NotesEvent()
    object OrderButtonClicked: NotesEvent()
    object DismissBottomSheet: NotesEvent()
    object AddNoteButtonClicked: NotesEvent()
    object DismissDialog: NotesEvent()
}