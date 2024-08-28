package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.Book
import com.example.booknote.domain.repository.BookRepository

class DeleteBook(
    private val repository: BookRepository
){
    suspend operator fun invoke(books: List<Book>){
        repository.deleteBook(books)
    }
}