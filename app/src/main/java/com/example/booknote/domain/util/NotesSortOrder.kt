package com.example.booknote.domain.util

sealed class NotesSortOrder {
    data object NoteTitleAsc : NotesSortOrder()
    data object NoteTitleDesc : NotesSortOrder()
    data object PageAsc : NotesSortOrder()
    data object PageDesc : NotesSortOrder()
    data object DateCreatedAsc : NotesSortOrder()
    data object DateCreatedDesc : NotesSortOrder()
}