package com.example.booknote.data.data_source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.booknote.domain.model.FocusSession
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(focusSession: FocusSession)

    @Query("SELECT * FROM focusSessions WHERE bookId = :bookId ORDER BY date DESC")
    fun getFocusSessionsByBookId(bookId: Long): Flow<List<FocusSession>>

    @Query("SELECT * FROM focusSessions WHERE date LIKE '%' || :date || '%'")
    fun getFocusSessionByDate(date: String): Flow<List<FocusSession>>


}