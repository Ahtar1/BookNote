package com.example.booknote.domain.use_case

data class NoteUseCases(
    val getNotes: GetNotes,
    val addNote: AddNote,
    val deleteNote: DeleteNote
    )