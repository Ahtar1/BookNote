package com.example.booknote.presentation.calendar

import android.annotation.SuppressLint
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknote.domain.model.Book
import com.example.booknote.domain.model.FocusSession
import com.example.booknote.domain.model.Note
import com.example.booknote.domain.use_case.BookUseCases
import com.example.booknote.domain.use_case.FocusSessionUseCases
import com.example.booknote.domain.use_case.NoteUseCases
import com.example.booknote.domain.util.BooksSortOrder
import com.mohamedrejeb.richeditor.model.RichTextState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val bookUseCases: BookUseCases,
    private val focusSessionUseCases: FocusSessionUseCases
): ViewModel() {

    private var _dates = mutableListOf<LocalDate>()
    val dates: MutableList<LocalDate> = _dates

    private val _notes = mutableStateOf<List<Note>>(emptyList())
    val notes: State<List<Note>> = _notes

    private val _focusSessions = mutableStateOf<List<FocusSession>>(emptyList())
    val focusSessions: State<List<FocusSession>> = _focusSessions

    private val _books = mutableStateOf<List<Book>>(emptyList())
    val books: State<List<Book>> = _books

    private val richTextStateMap = mutableMapOf<String, RichTextState>()

    init {
        viewModelScope.launch {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            noteUseCases.getNoteDates().collectLatest { it.forEach {
                _dates.add(LocalDate.parse(it, formatter))
            } }
        }
        viewModelScope.launch {
            noteUseCases.getNotesByDate(LocalDate.now().format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy")
            )).collectLatest { notesList ->
                _notes.value = notesList
            }
        }
        viewModelScope.launch {
            focusSessionUseCases.getFocusSessionByDate(LocalDate.now().format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy")
            )).collectLatest { focusSessionList ->
                _focusSessions.value = focusSessionList
            }
        }
        viewModelScope.launch {
            bookUseCases.getBooks("",BooksSortOrder.BookTitleDesc).collectLatest { booksList ->
                _books.value = booksList
            }
        }
    }

    fun getRichTextState(note: Note): RichTextState {
        return richTextStateMap.getOrPut(note.id.toString()) {
            RichTextState().apply {
                setHtml(note.noteText ?: "")
            }
        }
    }

    fun getBookNameById(bookId: Long): String {
        return _books.value.find { it.id == bookId }?.title ?: ""
    }

    @SuppressLint("DefaultLocale")
    fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    fun onEvent(event: CalendarEvent) {
        when(event){
            is CalendarEvent.GetNotes -> {
                viewModelScope.launch {

                    val stringDate = event.date.format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    )

                    noteUseCases.getNotesByDate(stringDate).collectLatest { notesList ->
                        _notes.value = notesList
                    }
                }
            }
            is CalendarEvent.GetFocusSessions -> {
                viewModelScope.launch {
                    val stringDate = event.date.format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    )

                    focusSessionUseCases.getFocusSessionByDate(stringDate).collectLatest { focusSessionList ->
                        _focusSessions.value = focusSessionList
                    }
                }
            }
        }
    }
}