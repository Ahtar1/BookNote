package com.example.booknote.domain.use_case

data class FocusSessionUseCases(
    val getFocusSessionsByBookId: GetFocusSessionByBookId,
    val addFocusSession: AddFocusSession,
    val getFocusSessionByDate: GetFocusSessionByDate,
)