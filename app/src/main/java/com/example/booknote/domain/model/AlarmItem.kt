package com.example.booknote.domain.model


import androidx.room.Entity
import java.time.LocalDateTime

@Entity(tableName = "alarms")
data class AlarmItem(
    val time: LocalDateTime,
    val message: String,
)