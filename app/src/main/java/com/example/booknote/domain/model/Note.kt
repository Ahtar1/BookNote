package com.example.booknote.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "notes",
    foreignKeys = [ForeignKey(
        entity = Book::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("bookId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val noteText: String,
    val page: Int,
    val dateCreated: Date,
    val favorite: Boolean = false,
    @ColumnInfo(index = true) val bookId: Long
)
