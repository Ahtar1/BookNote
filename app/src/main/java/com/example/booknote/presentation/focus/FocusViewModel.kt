package com.example.booknote.presentation.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknote.domain.model.Book
import com.example.booknote.domain.model.FocusSession
import com.example.booknote.domain.use_case.BookUseCases
import com.example.booknote.domain.use_case.FocusSessionUseCases
import com.example.booknote.domain.util.BooksSortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val bookUseCases: BookUseCases,
    private val focusSessionUseCases: FocusSessionUseCases,
):ViewModel() {

    private val _state = MutableStateFlow(FocusState())
    val state = _state.asStateFlow()

    private var timerJob: Job? = null

    fun onEvent(event: FocusEvent) {
        when (event) {
            is FocusEvent.GetBooks -> getBooks()
            is FocusEvent.SelectBook -> selectBook(event.book)
            is FocusEvent.SetDefaultSelectedBook -> setDefaultSelectedBook(event.bookId)
            is FocusEvent.SaveFocus -> saveFocus(event.focusSession)
            is FocusEvent.DeleteSelectedBook -> deleteSelectedBook()
            is FocusEvent.StartTimer -> startTimer()
            is FocusEvent.StopTimer -> stopTimer()
            is FocusEvent.Tick -> tick()
            is FocusEvent.ResetTimer -> resetTimer()
        }
    }

    private fun getBooks(){
        viewModelScope.launch {
            bookUseCases.getBooks("", BooksSortOrder.BookTitleDesc).collect { books ->
                _state.value = _state.value.copy(books = books)
            }
        }
    }

    private fun selectBook(book: Book) {
        _state.value = _state.value.copy(selectedBook = book)
    }

    private fun setDefaultSelectedBook(bookId: Long) {
        viewModelScope.launch {
            bookUseCases.getBooks("", BooksSortOrder.BookTitleDesc).collect {
                val book = it.find { book -> book.id == bookId }
                if (book != null) {
                    _state.value = _state.value.copy(selectedBook = book)
                }
            }
        }
    }

    private fun deleteSelectedBook() {
        viewModelScope.launch {
            _state.value = _state.value.copy(selectedBook = Book(
                id = -1,
                title = "",
                author = "",
                publisher = "",
                language = "",
                status = Book.BookStatus.TO_READ,
                bookImagePath = "",
            ))
        }
    }

    private fun saveFocus(focusSession: FocusSession) {
        viewModelScope.launch {
            focusSessionUseCases.addFocusSession(focusSession)
        }
    }
    private fun startTimer() {
        if (_state.value.isRunning) return
        _state.value = _state.value.copy(isRunning = true)
        timerJob = viewModelScope.launch {
            while (_state.value.isRunning) {
                delay(1000)
                onEvent(FocusEvent.Tick)
            }
        }
    }

    private fun stopTimer() {
        _state.value = _state.value.copy(isRunning = false)
        timerJob?.cancel()
    }

    private fun tick() {
        _state.value = _state.value.copy(elapsedSeconds = _state.value.elapsedSeconds + 1)
    }

    private fun resetTimer() {
        _state.value = _state.value.copy(elapsedSeconds = 0L)
    }
}