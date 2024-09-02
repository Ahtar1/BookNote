package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.Note
import com.example.booknote.domain.repository.NoteRepository

class DeleteNotes(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(notes: List<Note>){
        repository.deleteNotes(notes)
    }
}