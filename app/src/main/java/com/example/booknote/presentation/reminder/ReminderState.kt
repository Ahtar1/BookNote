package com.example.booknote.presentation.reminder

import com.example.booknote.domain.model.Reminder

data class ReminderState(
    val reminders: List<Reminder> = emptyList(),
)