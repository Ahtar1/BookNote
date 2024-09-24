package com.example.booknote.presentation.add_image

import com.example.booknote.domain.model.Note

sealed class AddImageEvent {
    data class AddImage(val note: Note): AddImageEvent()
}