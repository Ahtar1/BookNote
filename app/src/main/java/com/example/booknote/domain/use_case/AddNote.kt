package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.Note
import com.example.booknote.domain.repository.NoteRepository

class AddNote(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(note: Note){
        repository.insertNote(note)
    }
}