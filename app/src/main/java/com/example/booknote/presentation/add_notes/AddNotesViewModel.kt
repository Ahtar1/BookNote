package com.example.booknote.presentation.add_notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknote.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AddNotesState())
    var state: State<AddNotesState> = _state

    fun onEvent(event: AddNotesEvent) {
        when(event){

            is AddNotesEvent.AddNote -> {
                viewModelScope.launch {
                    if (event.note.id == 0L) {
                        println("noteId sıfır yeni not")
                        noteUseCases.addNote(event.note)
                    } else {
                        println("noteId 0 değil eski not")
                        noteUseCases.updateNote(event.note)
                    }
                }
            }
            is AddNotesEvent.GetNote -> {
                viewModelScope.launch {
                    println("noteTitle viewmodel: ${noteUseCases.getNote(event.noteId).noteTitle}")
                    _state.value = _state.value.copy(
                        note = noteUseCases.getNote(event.noteId)
                    )
                }
            }
        }
    }
}