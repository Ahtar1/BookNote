package com.example.booknote.presentation.add_audio

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknote.domain.use_case.NoteUseCases
import com.example.booknote.presentation.add_notes.AddNotesEvent
import com.example.booknote.presentation.add_notes.AddNotesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAudioViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    var isBottomSheetShown by mutableStateOf(false)
        private set

    private val _state = mutableStateOf(AddNotesState())
    var state: State<AddNotesState> = _state

    fun onEvent(event: AddAudioEvent) {
        when(event){

            is AddAudioEvent.AddAudio -> {
                viewModelScope.launch {
                    noteUseCases.addNote(event.note)
                }
            }

            is AddAudioEvent.SaveButtonClicked -> {
                isBottomSheetShown = true
            }

            is AddAudioEvent.DismissBottomSheet -> {
                isBottomSheetShown = false
            }
            is AddAudioEvent.SaveTags -> {
                viewModelScope.launch {
                    if (event.noteId != null) {
                        noteUseCases.updateNote(
                            state.value.note.copy(
                                tags = event.tags
                            )
                        )
                        _state.value = _state.value.copy(
                            note = state.value.note.copy(
                                tags = event.tags
                            )
                        )
                    } else {
                        _state.value = _state.value.copy(
                            note = state.value.note.copy(
                                tags = event.tags
                            )
                        )
                    }
                }
            }
        }
    }
}