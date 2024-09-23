package com.example.booknote.presentation.add_audio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknote.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAudioViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    fun onEvent(event: AddAudioEvent) {
        when(event){

            is AddAudioEvent.AddAudio -> {
                viewModelScope.launch {
                    noteUseCases.addNote(event.note)
                }
            }
        }
    }
}