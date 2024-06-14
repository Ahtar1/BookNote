package com.example.booknote.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.booknote.domain.model.Book
import com.example.booknote.domain.model.Note
import com.example.booknote.domain.model.relation.BookWithNotes
import kotlinx.coroutines.flow.Flow


@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: Long): Book

    @Query("SELECT * FROM books WHERE title LIKE '%' || :searchQuery || '%' OR author LIKE '%' || :searchQuery || '%'")
    fun getBooksBySearchQuery(searchQuery: String): Flow<List<Book>>

    @Delete
    suspend fun deleteBook(book: Book)
}