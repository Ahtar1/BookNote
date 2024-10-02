package com.example.booknote.domain.use_case

data class NoteUseCases(
    val getNotes: GetNotes,
    val addNote: AddNote,
    val updateNote: UpdateNote,
    val getNote: GetNote,
    val deleteNotes: DeleteNotes,
    val getNoteDates: GetNoteDates,
    val getNotesByDate: GetNotesByDate
    )