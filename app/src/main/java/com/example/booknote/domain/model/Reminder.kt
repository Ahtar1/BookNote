package com.example.booknote.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val message: String,
    val hour: Int,
    val minute: Int,
    val days: List<String>,
    val isActive: Boolean = true,
)

