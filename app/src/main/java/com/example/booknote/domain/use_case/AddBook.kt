package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.Book
import com.example.booknote.domain.repository.BookRepository

class AddBook(
    private val repository: BookRepository
) {
    suspend operator fun invoke(book: Book) {
        repository.insertBook(book)
    }
}