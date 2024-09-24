package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.Note
import com.example.booknote.domain.repository.NoteRepository
import com.example.booknote.domain.util.SortOrder
import kotlinx.coroutines.flow.Flow

class GetNotes(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(bookId: Long, searchQuery: String, sortOrder: SortOrder): Flow<List<Note>> {
        return repository.getNotesOfABook(bookId, searchQuery, sortOrder)
    }
}