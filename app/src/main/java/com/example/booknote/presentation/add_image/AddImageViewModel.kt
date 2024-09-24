package com.example.booknote.presentation.add_image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknote.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddImageViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    fun onEvent(event: AddImageEvent) {
        when(event){
            is AddImageEvent.AddImage -> {
                viewModelScope.launch {
                    noteUseCases.addNote(event.note)
                }
            }
        }
    }
}