package com.example.booknote.presentation.add_notes

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    var isColorPickerShown by mutableStateOf(false)
        private set

    val colorList = listOf(
        0xFFFFCDD2, // Light Red
        0xFFF8BBD0, // Light Pink
        0xFFE1BEE7, // Light Purple
        0xFFD1C4E9, // Light Deep Purple
        0xFFC5CAE9, // Light Indigo
        0xFFBBDEFB, // Light Blue
        0xFFB3E5FC, // Light Cyan
        0xFFB2EBF2, // Light Teal
        0xFFC8E6C9, // Light Green
        0xFFDCE775, // Lime
        0xFFFFF59D, // Yellow
        0xFFFFE082, // Amber
        0xFFFFCCBC, // Deep Orange
        0xFFD7CCC8, // Brown
        0xFFCFD8DC  // Blue Grey
    )

    fun onEvent(event: AddNotesEvent) {
        when(event){

            is AddNotesEvent.AddNote -> {
                viewModelScope.launch {
                    if (event.note.id == 0L) {
                        noteUseCases.addNote(event.note)
                    } else {
                        noteUseCases.updateNote(event.note)
                    }
                }
            }
            is AddNotesEvent.GetNote -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        note = noteUseCases.getNote(event.noteId)
                    )
                }
            }
            is AddNotesEvent.ChangeColor -> {
                _state.value = _state.value.copy(
                    note = state.value.note.copy(
                        color = event.color
                    )
                )
            }
            AddNotesEvent.ToggleColorPicker -> {
                isColorPickerShown = !isColorPickerShown
            }
        }
    }
}