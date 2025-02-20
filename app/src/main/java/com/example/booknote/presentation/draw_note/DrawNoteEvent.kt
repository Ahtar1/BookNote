package com.example.booknote.presentation.draw_note

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap

sealed class DrawNoteEvent {
    data class SaveNote(
        val bookId: Long,
        val title: String,
        val content: String,
        val image: Bitmap
    ): DrawNoteEvent()
}