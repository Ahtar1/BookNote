package com.example.booknote.presentation.add_book

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknote.domain.use_case.BookUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val bookUseCases: BookUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AddBookState())
    var state: State<AddBookState> = _state

    fun onEvent(event: AddBookEvent) {
        when (event) {
            is AddBookEvent.GetBook -> {
                viewModelScope.launch {
                    bookUseCases.getBookById(event.bookId).let { book ->
                        _state.value = _state.value.copy(
                            bookId = book.id,
                            title = book.title,
                            author = book.author,
                            publisher = book.publisher,
                            language = book.language,
                            status = book.status
                        )
                    }
                }
            }
            is AddBookEvent.SaveBook -> {
                viewModelScope.launch {
                    bookUseCases.addBook(event.book)
                }
            }
        }
    }
}