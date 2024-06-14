package com.example.booknote.domain.use_case

data class BookUseCases(
    val addBook: AddBook,
    val deleteBook: DeleteBook,
    val getBookById: GetBookById,
    val getBooks: GetBooks
)
