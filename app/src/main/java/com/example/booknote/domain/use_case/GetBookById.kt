package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.Book
import com.example.booknote.domain.repository.BookRepository

class GetBookById(
    private val repository: BookRepository
) {

    suspend operator fun invoke(id: Long): Book {
        return repository.getBookById(id)
    }
}