package com.example.booknote.presentation.draw_note

import android.content.Context
import android.graphics.Bitmap

sealed class DrawNoteEvent {
    data class SaveNote(
        val noteId: Long,
        val bookId: Long,
        val title: String,
        val page: Int,
        val color: Long,
        val image: Bitmap,
        val context: Context
    ): DrawNoteEvent()
    data class GetNote(
        val noteId: Long
    ): DrawNoteEvent(
    )
    data class SaveTags(val tags: List<String>, val noteId: Long?): DrawNoteEvent()
    data object SaveButtonClicked: DrawNoteEvent()
    data object DismissBottomSheet: DrawNoteEvent()
}