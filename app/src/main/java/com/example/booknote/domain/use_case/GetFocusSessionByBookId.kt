package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.FocusSession
import com.example.booknote.domain.repository.FocusSessionRepository
import kotlinx.coroutines.flow.Flow

class GetFocusSessionByBookId(
    private val repository: FocusSessionRepository
) {
    suspend operator fun invoke(bookId: Long): Flow<List<FocusSession>> {
        return repository.getFocusSessionsByBookId(bookId)
    }
}