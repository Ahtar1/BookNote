package com.example.booknote.domain.repository

import com.example.booknote.domain.model.Note
import com.example.booknote.domain.util.SortOrder
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    suspend fun insertNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun getNotesOfABook(bookId: Long, searchQuery: String, sortOrder: SortOrder): Flow<List<Note>>
    suspend fun getNoteById(noteId: Long): Note
    suspend fun deleteNotes(notes: List<Note>)
    suspend fun getAllNoteDates(): Flow<List<String>>
    suspend fun getNotesByDate(date: String): Flow<List<Note>>
}