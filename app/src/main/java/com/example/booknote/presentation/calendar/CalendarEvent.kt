package com.example.booknote.presentation.calendar

import java.time.LocalDate

sealed class CalendarEvent {
    data class GetNotes(val date: LocalDate): CalendarEvent()

}