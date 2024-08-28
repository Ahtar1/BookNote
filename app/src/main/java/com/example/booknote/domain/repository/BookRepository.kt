package com.example.booknote.domain.repository

import com.example.booknote.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    suspend fun insertBook(book: Book)
    suspend fun getBookById(id: Long): Book
    suspend fun getBooksBySearchQuery(searchQuery: String): Flow<List<Book>>
    suspend fun deleteBook(books: List<Book>)
}