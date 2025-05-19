package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.Note
import com.example.booknote.domain.repository.NoteRepository
import com.example.booknote.domain.util.SortOrder
import kotlinx.coroutines.flow.Flow

class GetFavoriteNotes(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(searchQuery: String, sortOrder: SortOrder): Flow<List<Note>> {
        return repository.getFavoriteNotes(searchQuery, sortOrder)
    }
}