package com.example.booknote.data.repository

import com.example.booknote.data.data_source.NoteDao
import com.example.booknote.domain.model.Note
import com.example.booknote.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl(
    private val dao: NoteDao
): NoteRepository {
    override suspend fun insertNote(note: Note) {
        dao.insertNote(note)
    }

    override suspend fun getNotesOfABook(bookId: Long, searchQuery: String): Flow<List<Note>> {
        return dao.getNotesOfABook(bookId, searchQuery)
    }

    override suspend fun deleteNote(note: Note) {
        dao.deleteNote(note)
    }

}