package com.example.booknote.data.repository

import com.example.booknote.data.data_source.NoteDao
import com.example.booknote.domain.model.Note
import com.example.booknote.domain.repository.NoteRepository
import com.example.booknote.domain.util.NotesSortOrder
import com.example.booknote.domain.util.SortOrder
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl(
    private val dao: NoteDao
): NoteRepository {
    override suspend fun insertNote(note: Note) {
        dao.insertNote(note)
    }

    override suspend fun updateNote(note: Note) {
        dao.updateNote(note)
    }

    override suspend fun getNotesOfABook(bookId: Long, searchQuery: String, sortOrder: SortOrder): Flow<List<Note>> {
        val notesSortOrderString = when (sortOrder) {
            is NotesSortOrder.NoteTitleAsc -> "noteTitleAsc"
            is NotesSortOrder.NoteTitleDesc -> "noteTitleDesc"
            is NotesSortOrder.PageAsc -> "pageAsc"
            is NotesSortOrder.PageDesc -> "pageDesc"
            is NotesSortOrder.DateCreatedAsc -> "dateCreatedAsc"
            is NotesSortOrder.DateCreatedDesc -> "dateCreatedDesc"
            else -> throw IllegalArgumentException("Unsupported sort order: $sortOrder")
        }
        return dao.getNotesOfABook(bookId, searchQuery, notesSortOrderString)
    }

    override suspend fun getNoteById(noteId: Long): Note {
        return dao.getNoteById(noteId)
    }

    override suspend fun deleteNotes(notes: List<Note>) {
        dao.deleteNote(notes)
    }

}