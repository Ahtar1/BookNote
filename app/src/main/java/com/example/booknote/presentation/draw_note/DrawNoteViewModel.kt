package com.example.booknote.presentation.draw_note

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DrawNoteViewModel @Inject  constructor(

): ViewModel() {

    fun onEvent(event: DrawNoteEvent) {
        when (event) {
            is DrawNoteEvent.SaveNote -> {

            }
        }
    }
}