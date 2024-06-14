package com.example.booknote.domain.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.booknote.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    suspend fun insertNote(note: Note)

    suspend fun getNotesOfABook(bookId: Long, searchQuery: String): Flow<List<Note>>

    suspend fun deleteNote(note: Note)
}