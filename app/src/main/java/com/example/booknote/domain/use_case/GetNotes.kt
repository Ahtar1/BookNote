package com.example.booknote.domain.use_case

import com.example.booknote.domain.repository.NoteRepository

class GetNotes(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(bookId: Long, searchQuery: String){
        repository.getNotesOfABook(bookId, searchQuery)
    }
}