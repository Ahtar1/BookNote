package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.Reminder
import com.example.booknote.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow

class GetAllReminders(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(): Flow<List<Reminder>> {
        return repository.getAllReminders()
    }
}