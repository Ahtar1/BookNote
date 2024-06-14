package com.example.booknote.presentation.books

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknote.domain.model.Book
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

    private var recentlyDeletedBook: Book? = null

    var isDialogShown by mutableStateOf(false)
        private set

    fun onDismissDialog(){
        isDialogShown = false
    }

    init {
        viewModelScope.launch {
            bookUseCases.getBooks("").collectLatest {
                _state.value = _state.value.copy(
                    books = it,
                    searchQuery = _state.value.searchQuery
                )
            }
        }
    }

    fun onEvent(event: BooksEvent) {
        when(event){
            is BooksEvent.DeleteBook -> {
                viewModelScope.launch {
                    bookUseCases.deleteBook(event.book)
                    recentlyDeletedBook = event.book
                }
            }
            is BooksEvent.GetBooks -> {
                viewModelScope.launch {
                    println("Getbooks")
                    bookUseCases.getBooks(event.searchQuery).collectLatest {
                        _state.value = _state.value.copy(
                            books = it,
                            searchQuery = event.searchQuery
                        )
                    }
                }
            }
            is BooksEvent.RestoreBook -> {
                viewModelScope.launch {
                    bookUseCases.addBook(recentlyDeletedBook ?: return@launch)
                    recentlyDeletedBook = null
                }
            }

            is BooksEvent.AddBook -> {
                viewModelScope.launch {
                    bookUseCases.addBook(event.book)
                }
            }

            is BooksEvent.AddBookButtonClicked -> {
                isDialogShown = true
            }

            is BooksEvent.DismissDialog -> {
                isDialogShown = false
            }
        }
    }
}