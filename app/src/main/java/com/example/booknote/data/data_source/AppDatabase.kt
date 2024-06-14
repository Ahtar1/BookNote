package com.example.booknote.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.booknote.domain.model.Book
import com.example.booknote.domain.model.Note

@Database(entities = [Book::class, Note::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val bookDao: BookDao
    abstract val noteDao: NoteDao

    companion object {
        const val DATABASE_NAME = "book_note_db"
    }
}