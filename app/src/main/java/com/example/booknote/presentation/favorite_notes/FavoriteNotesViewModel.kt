package com.example.booknote.presentation.favorite_notes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknote.domain.model.Note
import com.example.booknote.domain.use_case.BookUseCases
import com.example.booknote.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FavoriteNotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val bookUseCases: BookUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteNotesState())
    val state = _state.asStateFlow()

    var isBottomSheetShown by mutableStateOf(false)
        private set

    private var notes = mutableListOf<Note>()

    fun onEvent(event: FavoriteNotesEvent) {
        when(event){
            is FavoriteNotesEvent.DeleteNotes -> {
                viewModelScope.launch {
                    noteUseCases.deleteNotes(event.notes)
                    event.notes.forEach { note ->
                        note.imageFilePath?.let {
                            deleteImageFromStorage(note.imageFilePath)
                        }
                    }
                }
            }
            is FavoriteNotesEvent.GetFavoriteNotes -> {
                viewModelScope.launch {
                    noteUseCases.getFavoriteNotes(event.searchQuery, state.value.order).collectLatest {
                        notes = it.toMutableList()
                        _state.value = _state.value.copy(
                            notes = it,
                            searchQuery = event.searchQuery,
                            order = state.value.order
                        )
                    }
                }
            }

            is FavoriteNotesEvent.ChangeOrder -> {
                _state.value = _state.value.copy(
                    order = event.order
                )
            }

            is FavoriteNotesEvent.OrderButtonClicked -> {
                isBottomSheetShown = true
            }

            is FavoriteNotesEvent.DismissBottomSheet -> {
                isBottomSheetShown = false
            }

            is FavoriteNotesEvent.GetTags -> {
                viewModelScope.launch {
                    noteUseCases.getTags().collectLatest { rawTagStrings ->
                        val allTags = rawTagStrings.flatMap { jsonString ->
                            try {
                                val jsonArray = JSONArray(jsonString)
                                List(jsonArray.length()) { i -> jsonArray.getString(i) }
                            } catch (e: Exception) {
                                emptyList()
                            }
                        }.distinct()

                        _state.value = _state.value.copy(
                            tags = allTags
                        )
                    }
                }
            }
            is FavoriteNotesEvent.UpdateNotesByTags -> {
                if(event.tags.isEmpty()){
                    _state.value = _state.value.copy(
                        notes = notes
                    )
                    return
                }

                val filteredNotes = notes.filter { note ->
                    event.tags.any { tag -> tag in note.tags }
                }
                _state.value = _state.value.copy(notes = filteredNotes)
            }
            is FavoriteNotesEvent.ChangeFavorite -> {
                viewModelScope.launch {
                    noteUseCases.updateNote(event.note.copy(favorite = !event.note.favorite))
                }
            }
        }
    }

    fun loadBookTitle(bookId: Long) {
        viewModelScope.launch {
            val book = bookUseCases.getBookById(bookId)
            _state.value = _state.value.copy(
                bookTitles = _state.value.bookTitles + (bookId to book.title)
            )
        }
    }

    private fun deleteImageFromStorage(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }

}