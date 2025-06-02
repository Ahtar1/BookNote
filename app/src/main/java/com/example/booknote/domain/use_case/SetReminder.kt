package com.example.booknote.domain.use_case

import com.example.booknote.domain.model.AlarmItem
import com.example.booknote.domain.repository.AlarmScheduler
import java.time.LocalDateTime

class SetReminder(
    private val reminderRepository: AlarmScheduler
) {
    operator fun invoke(time : LocalDateTime, message: String) {
        reminderRepository.schedule(
            AlarmItem(
                time = time,
                message = message
            )
        )
    }
}
