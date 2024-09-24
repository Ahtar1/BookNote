package com.example.booknote.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.booknote.domain.model.Book
import kotlinx.coroutines.flow.Flow


@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: Long): Book

    @Query("""
        SELECT * FROM books 
        WHERE title LIKE '%' || :searchQuery || '%' OR author LIKE '%' || :searchQuery || '%'
        ORDER BY
        CASE
            WHEN :sortOrder = 'bookTitleAsc' THEN title COLLATE NOCASE END ASC,
            CASE
            WHEN :sortOrder = 'bookTitleDesc' THEN title COLLATE NOCASE END DESC,
            CASE
            WHEN :sortOrder = 'authorAsc' THEN author COLLATE NOCASE END ASC,
            CASE
            WHEN :sortOrder = 'authorDesc' THEN author COLLATE NOCASE END DESC,
            CASE
            WHEN :sortOrder = 'languageAsc' THEN language COLLATE NOCASE END ASC,
            CASE
            WHEN :sortOrder = 'languageDesc' THEN language COLLATE NOCASE END DESC""")
    fun getBooksBySearchQuery(searchQuery: String, sortOrder: String): Flow<List<Book>>

    @Delete
    suspend fun deleteBook(books: List<Book>)
}