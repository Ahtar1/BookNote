package com.example.booknote.di

import android.app.Application
import androidx.room.Room
import com.example.booknote.data.data_source.AppDatabase
import com.example.booknote.data.repository.BookRepositoryImpl
import com.example.booknote.data.repository.NoteRepositoryImpl
import com.example.booknote.domain.repository.BookRepository
import com.example.booknote.domain.repository.NoteRepository
import com.example.booknote.domain.use_case.AddBook
import com.example.booknote.domain.use_case.AddNote
import com.example.booknote.domain.use_case.BookUseCases
import com.example.booknote.domain.use_case.DeleteBook
import com.example.booknote.domain.use_case.DeleteNotes
import com.example.booknote.domain.use_case.GetBookById
import com.example.booknote.domain.use_case.GetBooks
import com.example.booknote.domain.use_case.GetNote
import com.example.booknote.domain.use_case.GetNoteDates
import com.example.booknote.domain.use_case.GetNotes
import com.example.booknote.domain.use_case.GetNotesByDate
import com.example.booknote.domain.use_case.NoteUseCases
import com.example.booknote.domain.use_case.UpdateNote
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase{
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(db: AppDatabase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao)
    }

    @Provides
    @Singleton
    fun provideBookRepository(db: AppDatabase): BookRepository {
        return BookRepositoryImpl(db.bookDao)
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotes(repository),
            addNote = AddNote(repository),
            updateNote = UpdateNote(repository),
            deleteNotes = DeleteNotes(repository),
            getNote = GetNote(repository),
            getNoteDates = GetNoteDates(repository),
            getNotesByDate = GetNotesByDate(repository)
        )
    }
    @Provides
    @Singleton
    fun provideBookUseCases(repository: BookRepository): BookUseCases {
        return BookUseCases(
            getBooks = GetBooks(repository),
            addBook = AddBook(repository),
            deleteBooks = DeleteBook(repository),
            getBookById = GetBookById(repository)
        )
    }
}