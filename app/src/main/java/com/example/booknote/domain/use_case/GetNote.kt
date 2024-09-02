package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.Note
import com.example.booknote.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNote(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(noteId: Long): Note {
        return repository.getNoteById(noteId)
    }
}