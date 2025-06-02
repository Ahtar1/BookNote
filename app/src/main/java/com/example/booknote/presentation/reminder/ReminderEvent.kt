package com.example.booknote.presentation.reminder

import com.example.booknote.domain.model.Reminder

sealed class ReminderEvent {
    data class ChangeReminderStatus(val reminder: Reminder) : ReminderEvent()
    data class DeleteReminder(val reminder: Reminder) : ReminderEvent()
}