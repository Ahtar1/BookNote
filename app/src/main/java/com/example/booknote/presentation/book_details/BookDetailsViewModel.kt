package com.example.booknote.presentation.book_details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknote.domain.use_case.BookUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val bookUseCases: BookUseCases
) : ViewModel() {

    private val _state = mutableStateOf(BookDetailsState())
    var state: State<BookDetailsState> = _state

    fun onEvent(event: BookDetailsEvent) {
        when (event) {
            is BookDetailsEvent.GetBookDetails -> {
                viewModelScope.launch {
                    bookUseCases.getBookById(event.bookId).let { book ->
                        _state.value = _state.value.copy(
                            bookId = book.id,
                            title = book.title,
                            author = book.author,
                            publisher = book.publisher,
                            language = book.language,
                            status = book.status,
                            bookImageFilePath = book.bookImagePath
                        )
                    }
                }
            }
            is BookDetailsEvent.UpdateBook -> {
                viewModelScope.launch {
                    bookUseCases.updateBook(event.book)
                }
            }
            is BookDetailsEvent.UpdateBookImage -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        bookImageFilePath = event.bookImageFilePath
                    )
                }
            }
        }
    }
}