package com.example.booknote.presentation.notes

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknote.domain.use_case.BookUseCases
import com.example.booknote.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val bookUseCases: BookUseCases
) : ViewModel() {

    private val _state = mutableStateOf(NotesState())
    var state: State<NotesState> = _state
    var isDialogShown by mutableStateOf(false)
        private set

    var isBottomSheetShown by mutableStateOf(false)
        private set
    fun onEvent(event: NotesEvent) {
        when(event){
            is NotesEvent.DeleteNotes -> {
                viewModelScope.launch {
                    noteUseCases.deleteNotes(event.notes)
                }
            }
            is NotesEvent.GetNotes -> {
                viewModelScope.launch {
                    println("GetNotes order: ${state.value.order}")
                    println("GetNotes searchquery: ${event.searchQuery}")
                    println("GetNotes bookid: ${event.bookId}")
                    noteUseCases.getNotes(bookId = event.bookId.toLong(),event.searchQuery, state.value.order).collectLatest {
                        println("note Size GetNotes viewmodel: ${it.size}")
                        _state.value = _state.value.copy(
                            notes = it,
                            searchQuery = event.searchQuery,
                            order = state.value.order
                        )
                    }
                }
            }
            is NotesEvent.GetAudios -> {
                viewModelScope.launch {
                    findFilesByBookId(event.context, event.bookId.toInt()).forEach { file ->
                        _state.value = _state.value.copy(
                            audios = _state.value.audios + file
                        )
                    }
                }
            }

            is NotesEvent.AddNote -> {
                viewModelScope.launch {
                    noteUseCases.addNote(event.note)
                }
            }

            is NotesEvent.ChangeOrder -> {
                _state.value = _state.value.copy(
                    order = event.order
                )
            }

            is NotesEvent.AddNoteButtonClicked -> {
                isDialogShown = true
            }

            is NotesEvent.OrderButtonClicked -> {
                isBottomSheetShown = true
            }

            is NotesEvent.InfoButtonClicked -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        book = bookUseCases.getBookById(event.bookId)
                    )
                }
                isDialogShown = true
            }

            is NotesEvent.DismissBottomSheet -> {
                isBottomSheetShown = false
            }

            is NotesEvent.DismissDialog -> {
                isDialogShown = false
            }
        }
    }

    fun findFilesByBookId(context: Context, bookId: Int): List<File> {
        val externalDir = context.getExternalFilesDir(null)
        val files = mutableListOf<File>()
        return listOf(File(context.getExternalFilesDir(null), "audio_${bookId}_aaa.mp3"))
        /*
        externalDir?.listFiles()?.forEach { file ->
            if (file.name.contains("audio_${bookId}_") && file.name.endsWith(".mp3")) {
                files.add(file)
            }
        }
        return files

         */
    }

}