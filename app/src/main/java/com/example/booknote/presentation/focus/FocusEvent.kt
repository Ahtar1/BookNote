package com.example.booknote.presentation.focus

import com.example.booknote.domain.model.Book
import com.example.booknote.domain.model.FocusSession

sealed class FocusEvent {
    data object GetBooks : FocusEvent()
    data class SelectBook(val book: Book) : FocusEvent()
    data class SetDefaultSelectedBook(val bookId: Long) : FocusEvent()
    data class SaveFocus(val focusSession: FocusSession) : FocusEvent()
    data object DeleteSelectedBook : FocusEvent()
    data object StartTimer : FocusEvent()
    data object StopTimer : FocusEvent()
    data object ResetTimer : FocusEvent()
    data object Tick : FocusEvent()
}