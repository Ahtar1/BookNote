package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.Book
import com.example.booknote.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow

class GetBooks(
    private val repository: BookRepository
) {
    suspend operator fun invoke(searchQuery: String): Flow<List<Book>> {
        return repository.getBooksBySearchQuery(searchQuery)
    }
}