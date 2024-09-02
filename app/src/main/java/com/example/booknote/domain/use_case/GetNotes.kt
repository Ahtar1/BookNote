package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.Note
import com.example.booknote.domain.repository.NoteRepository
import com.example.booknote.domain.util.NotesSortOrder
import kotlinx.coroutines.flow.Flow

class GetNotes(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(bookId: Long, searchQuery: String, notesSortOrder: NotesSortOrder): Flow<List<Note>> {
        return repository.getNotesOfABook(bookId, searchQuery, notesSortOrder)
    }
}