package com.example.booknote.domain.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.booknote.domain.model.Book
import com.example.booknote.domain.model.relation.BookWithNotes
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    suspend fun insertBook(book: Book)
    suspend fun getBookById(id: Long): Book

    suspend fun getBooksBySearchQuery(searchQuery: String): Flow<List<Book>>

    suspend fun deleteBook(book: Book)
}