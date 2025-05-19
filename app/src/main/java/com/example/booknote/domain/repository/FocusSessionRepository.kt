package com.example.booknote.domain.repository

import com.example.booknote.domain.model.FocusSession
import kotlinx.coroutines.flow.Flow

interface FocusSessionRepository {
    suspend fun insertFocusSession(focusSession: FocusSession)
    suspend fun getFocusSessionsByBookId(bookId: Long): Flow<List<FocusSession>>
    suspend fun getFocusSessionByDate(date: String): Flow<List<FocusSession>>
}