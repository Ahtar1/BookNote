package com.example.booknote.presentation.draw_note

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknote.domain.model.Note
import com.example.booknote.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DrawNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
): ViewModel() {

    var isBottomSheetShown by mutableStateOf(false)
        private set

    private val _state = mutableStateOf(DrawNoteState())
    var state: State<DrawNoteState> = _state

    fun onEvent(event: DrawNoteEvent) {
        when (event) {
            is DrawNoteEvent.SaveNote -> {
                viewModelScope.launch {
                    if (event.noteId != 0L) {
                        val oldNote = noteUseCases.getNote(event.noteId)
                        val updatedFilePath = oldNote.imageFilePath?.let { filePath ->
                            saveBitmapToExistingFile(event.image, filePath)
                        } ?: saveBitmapToInternalStorage(event.context, event.image)

                        noteUseCases.updateNote(
                            Note(
                                id = event.noteId,
                                bookId = event.bookId,
                                noteTitle = event.title,
                                noteText = null,
                                imageFilePath = updatedFilePath,
                                dateCreated = LocalDateTime.now().format(
                                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                                ),
                                page = event.page,
                                isDrawn = true
                            )
                        )
                    } else {
                        val filePath = saveBitmapToInternalStorage(event.context, event.image)
                        noteUseCases.addNote(
                            Note(
                                bookId = event.bookId,
                                noteTitle = event.title,
                                noteText = null,
                                imageFilePath = filePath,
                                dateCreated = LocalDateTime.now().format(
                                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                                ),
                                page = event.page,
                                isDrawn = true
                            )
                        )
                    }
                }
            }

            is DrawNoteEvent.SaveButtonClicked -> {
                isBottomSheetShown = true
            }
            is DrawNoteEvent.DismissBottomSheet -> {
                isBottomSheetShown = false
            }

            is DrawNoteEvent.GetNote -> {
                viewModelScope.launch {
                    noteUseCases.getNote(event.noteId)
                    _state.value = _state.value.copy(
                        note = noteUseCases.getNote(event.noteId)
                    )
                }
            }
        }
    }

    private fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): String? {
        return try {
            val fileName = createUniqueFileName()
            val file = File(context.filesDir, "$fileName.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveBitmapToExistingFile(bitmap: Bitmap, filePath: String): String? {
        return try {
            val file = File(filePath)
            if (file.exists()) file.delete()

            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun createUniqueFileName(): String {
        return System.currentTimeMillis().toString()
    }
}