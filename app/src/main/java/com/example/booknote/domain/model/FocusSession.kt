package com.example.booknote.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "focusSessions",
    foreignKeys = [ForeignKey(
        entity = Book::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("bookId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class FocusSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val duration: Long,
    val date: String,
    @ColumnInfo(index = true) val bookId: Long?
)