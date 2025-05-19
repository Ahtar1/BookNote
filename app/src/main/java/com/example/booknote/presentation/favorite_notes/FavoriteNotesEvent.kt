package com.example.booknote.presentation.favorite_notes

import com.example.booknote.domain.model.Note
import com.example.booknote.domain.util.NotesSortOrder
import com.example.booknote.domain.util.SortOrder

sealed class FavoriteNotesEvent {
    data class DeleteNotes(val notes: List<Note>): FavoriteNotesEvent()
    data class GetFavoriteNotes(val searchQuery: String, val notesSortOrder: SortOrder = NotesSortOrder.DateCreatedAsc): FavoriteNotesEvent()
    data class ChangeOrder(val order: SortOrder): FavoriteNotesEvent()
    data class UpdateNotesByTags(val tags: List<String>): FavoriteNotesEvent()
    data class ChangeFavorite(val note: Note): FavoriteNotesEvent()
    object GetTags: FavoriteNotesEvent()
    object OrderButtonClicked: FavoriteNotesEvent()
    object DismissBottomSheet: FavoriteNotesEvent()
}