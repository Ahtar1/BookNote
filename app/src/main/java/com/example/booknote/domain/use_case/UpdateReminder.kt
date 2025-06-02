package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.Reminder
import com.example.booknote.domain.repository.ReminderRepository

class UpdateReminder(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(reminder: Reminder){
        repository.update(reminder)
    }
}