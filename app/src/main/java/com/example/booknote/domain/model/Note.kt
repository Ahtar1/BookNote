package com.example.booknote.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    val noteTitle: String,
    val noteText: String? = null,
    val audioFilePath: String? = null,
    val imageFilePath: String? = null,
    val page: Int,
    val dateCreated: String,
    val favorite: Boolean = false,
    @ColumnInfo(index = true) val bookId: Long
)
