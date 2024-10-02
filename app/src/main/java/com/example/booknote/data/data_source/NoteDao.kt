package com.example.booknote.data.data_source

import androidx.room.*
import com.example.booknote.domain.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query("""
    SELECT * FROM notes 
    WHERE bookId = :bookId 
    AND (noteText LIKE '%' || :searchQuery || '%' OR noteTitle LIKE '%' || :searchQuery || '%')
    ORDER BY 
    CASE 
        WHEN :sortOrder = 'noteTitleAsc' THEN noteTitle COLLATE NOCASE END ASC,
        CASE 
        WHEN :sortOrder = 'noteTitleDesc' THEN noteTitle COLLATE NOCASE END DESC,
        CASE 
        WHEN :sortOrder = 'pageAsc' THEN page END ASC,
        CASE 
        WHEN :sortOrder = 'pageDesc' THEN page END DESC,
        CASE 
        WHEN :sortOrder = 'dateCreatedAsc' THEN dateCreated END ASC,
        CASE 
        WHEN :sortOrder = 'dateCreatedDesc' THEN dateCreated END DESC
""")
    fun getNotesOfABook(bookId: Long, searchQuery: String, sortOrder: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): Note

    @Delete
    suspend fun deleteNote(note: List<Note>)

    @Query("SELECT DISTINCT dateCreated FROM notes")
    fun getAllNoteDates(): Flow<List<String>>// New query to get all note dates

    @Query("SELECT * FROM notes WHERE dateCreated LIKE '%' || :date || '%'")
    fun getNotesByDate(date: String): Flow<List<Note>>

}
