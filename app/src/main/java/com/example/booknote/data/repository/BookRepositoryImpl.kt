package com.example.booknote.data.repository

import com.example.booknote.data.data_source.BookDao
import com.example.booknote.domain.model.Book
import com.example.booknote.domain.repository.BookRepository
import com.example.booknote.domain.util.BooksSortOrder
import com.example.booknote.domain.util.SortOrder
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

    override suspend fun getBooksBySearchQuery(searchQuery: String, sortOrder: SortOrder): Flow<List<Book>> {
        val booksSortOrderString = when (sortOrder) {
            is BooksSortOrder.BookTitleAsc -> "bookTitleAsc"
            is BooksSortOrder.BookTitleDesc -> "bookTitleDesc"
            is BooksSortOrder.AuthorAsc -> "authorAsc"
            is BooksSortOrder.AuthorDesc -> "authorDesc"
            is BooksSortOrder.LanguageAsc -> "languageAsc"
            is BooksSortOrder.LanguageDesc -> "languageDesc"
            else -> throw IllegalArgumentException("Unsupported sort order: $sortOrder")
        }
        return dao.getBooksBySearchQuery(searchQuery,booksSortOrderString)
    }

    override suspend fun deleteBook(books: List<Book>) {
        dao.deleteBook(books)
    }
}