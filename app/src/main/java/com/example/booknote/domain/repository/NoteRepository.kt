package com.example.booknote.domain.repository

import com.example.booknote.domain.model.Note
import com.example.booknote.domain.util.NotesSortOrder
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    suspend fun insertNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun getNotesOfABook(bookId: Long, searchQuery: String, notesSortOrder: NotesSortOrder): Flow<List<Note>>
    suspend fun getNoteById(noteId: Long): Note
    suspend fun deleteNotes(note: List<Note>)
}