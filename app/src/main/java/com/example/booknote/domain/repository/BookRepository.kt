package com.example.booknote.domain.repository

import com.example.booknote.domain.model.Book
import com.example.booknote.domain.util.SortOrder
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    suspend fun insertBook(book: Book)
    suspend fun getBookById(id: Long): Book
    suspend fun getBooksBySearchQuery(searchQuery: String, sortOrder: SortOrder): Flow<List<Book>>
    suspend fun deleteBook(books: List<Book>)
}