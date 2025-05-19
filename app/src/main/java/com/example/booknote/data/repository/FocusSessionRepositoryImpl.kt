package com.example.booknote.data.repository

import com.example.booknote.data.data_source.FocusSessionDao
import com.example.booknote.domain.model.FocusSession
import com.example.booknote.domain.repository.FocusSessionRepository
import kotlinx.coroutines.flow.Flow

class FocusSessionRepositoryImpl(
    private val dao: FocusSessionDao
): FocusSessionRepository {
    override suspend fun insertFocusSession(focusSession: FocusSession) {
        dao.insertFocusSession(focusSession)
    }

    override suspend fun getFocusSessionsByBookId(bookId: Long): Flow<List<FocusSession>> {
        return dao.getFocusSessionsByBookId(bookId)
    }

    override suspend fun getFocusSessionByDate(date: String): Flow<List<FocusSession>> {
        return dao.getFocusSessionByDate(date)
    }
}