package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.Note
import com.example.booknote.domain.repository.NoteRepository

class UpdateNote(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(note: Note){
        repository.updateNote(note)
    }
}