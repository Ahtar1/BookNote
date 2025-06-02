package com.example.booknote.domain.repository

import com.example.booknote.domain.model.AlarmItem

interface AlarmScheduler {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}