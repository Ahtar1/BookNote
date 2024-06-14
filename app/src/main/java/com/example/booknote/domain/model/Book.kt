package com.example.booknote.domain.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val author: String,
    val publisher: String,
    val language: String,
) {
    @Ignore
    val notes: MutableList<Note> = mutableListOf()
}
