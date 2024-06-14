package com.example.booknote.data.repository

import com.example.booknote.data.data_source.BookDao
import com.example.booknote.domain.model.Book
import com.example.booknote.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow

class BookRepositoryImpl(
    private val dao: BookDao
): BookRepository {
    override suspend fun insertBook(book: Book){
        dao.insertBook(book)
    }

    override suspend fun getBookById(id: Long): Book {
        return dao.getBookById(id)
    }

    override suspend fun getBooksBySearchQuery(searchQuery: String): Flow<List<Book>> {
        return dao.getBooksBySearchQuery(searchQuery)
    }

    override suspend fun deleteBook(book: Book) {
        dao.deleteBook(book)
    }

}