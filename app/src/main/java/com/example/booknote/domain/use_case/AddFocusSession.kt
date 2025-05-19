package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.FocusSession
import com.example.booknote.domain.repository.FocusSessionRepository

class AddFocusSession(
    val repository: FocusSessionRepository
) {
    suspend operator fun invoke(focusSession: FocusSession) {
        repository.insertFocusSession(focusSession)
    }
}