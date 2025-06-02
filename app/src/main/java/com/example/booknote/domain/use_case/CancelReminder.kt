package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.AlarmItem
import com.example.booknote.domain.repository.AlarmScheduler
import java.time.LocalDateTime

class CancelReminder(
    private val reminderRepository: AlarmScheduler
) {
    operator fun invoke(time : LocalDateTime, message: String) {
       reminderRepository.cancel(
            AlarmItem(
                time = time,
                message = message
            )
        )
    }
}
