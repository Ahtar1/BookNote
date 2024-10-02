package com.example.booknote.presentation.calendar

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknote.domain.model.Note
import com.example.booknote.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
): ViewModel() {

    private var _dates = mutableListOf<LocalDate>()
    val dates: MutableList<LocalDate> = _dates

    private val _notes = mutableStateOf<List<Note>>(emptyList())
    val notes: State<List<Note>> = _notes
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
        }
    }
}