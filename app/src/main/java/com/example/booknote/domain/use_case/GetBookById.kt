package com.example.booknote.domain.use_case

import com.example.booknote.domain.repository.BookRepository

class GetBookById(
    private val repository: BookRepository
) {

    suspend operator fun invoke(id: Long){
        repository.getBookById(id)
    }
}