package com.example.booknote.presentation.add_audio

import com.example.booknote.domain.model.Note

sealed class AddAudioEvent {
    data class AddAudio(val note: Note): AddAudioEvent()
}