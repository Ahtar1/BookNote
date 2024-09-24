package com.example.booknote.presentation.books

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknote.domain.use_case.BookUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val bookUseCases: BookUseCases
) : ViewModel() {

    private val _state = mutableStateOf(BooksState())
    var state: State<BooksState> = _state


    var isDialogShown by mutableStateOf(false)
        private set

    var isBottomSheetShown by mutableStateOf(false)
        private set


    init {
        viewModelScope.launch {
            bookUseCases.getBooks("", state.value.order).collectLatest {
                _state.value = _state.value.copy(
                    books = it,
                    searchQuery = _state.value.searchQuery,
                    order = _state.value.order
                )
            }
        }
    }

    fun onEvent(event: BooksEvent) {
        when(event){
            is BooksEvent.DeleteBook -> {
                viewModelScope.launch {
                    bookUseCases.deleteBooks(event.books)
                }
            }
            is BooksEvent.GetBooks -> {
                viewModelScope.launch {
                    println("Getbooks")
                    bookUseCases.getBooks(event.searchQuery, state.value.order).collectLatest {
                        _state.value = _state.value.copy(
                            books = it,
                            searchQuery = event.searchQuery,
                            order = state.value.order
                        )
                    }
                }
            }
            is BooksEvent.AddBook -> {
                viewModelScope.launch {
                    bookUseCases.addBook(event.book)
                }
            }

            is BooksEvent.OrderButtonClicked -> {
                isBottomSheetShown = true
            }

            is BooksEvent.AddBookButtonClicked -> {
                isDialogShown = true
            }

            is BooksEvent.DismissDialog -> {
                isDialogShown = false
            }

            is BooksEvent.ChangeOrder -> {
                _state.value = _state.value.copy(
                    order = event.order
                )
            }

            is BooksEvent.DismissBottomSheet -> {
                isBottomSheetShown = false
            }
        }
    }
}