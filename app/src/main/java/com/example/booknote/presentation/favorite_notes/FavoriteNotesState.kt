package com.example.booknote.presentation.favorite_notes

import com.example.booknote.domain.model.Note
import com.example.booknote.domain.util.NotesSortOrder
import com.example.booknote.domain.util.SortOrder
import java.io.File

data class FavoriteNotesState(
    val notes: List<Note> = emptyList(),
    val audios: List<File> = emptyList(),
    val searchQuery: String = "",
    val order: SortOrder = NotesSortOrder.DateCreatedDesc,
    val tags : List<String> = emptyList(),
    val bookTitles: Map<Long,String> = emptyMap()
)