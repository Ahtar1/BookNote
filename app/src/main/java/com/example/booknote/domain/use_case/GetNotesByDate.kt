package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.Note
import com.example.booknote.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNotesByDate(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(date: String): Flow<List<Note>> {
        return repository.getNotesByDate(date)
    }
}