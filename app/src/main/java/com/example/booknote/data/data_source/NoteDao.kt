package com.example.booknote.data.data_source

import androidx.room.*
import com.example.booknote.domain.model.Book
import com.example.booknote.domain.model.Note
import kotlinx.coroutines.flow.Flow


@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Query("SELECT * FROM notes WHERE bookId = :bookId AND noteText LIKE '%' || :searchQuery || '%'")
    fun getNotesOfABook(bookId: Long, searchQuery: String): Flow<List<Note>>

    @Delete
    suspend fun deleteNote(note: Note)
}
