package com.example.booknote.presentation.add_reminder

import java.time.LocalDateTime

sealed class AddReminderEvent {
    data class SetReminder(val time: LocalDateTime, val days: List<String>, val message: String) : AddReminderEvent()
}