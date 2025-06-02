package com.example.booknote.domain.use_case

data class ReminderUseCases(
    val addReminder: AddReminder,
    val deleteReminder: DeleteReminder,
    val updateReminder: UpdateReminder,
    val getReminders: GetAllReminders
)