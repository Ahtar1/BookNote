package com.example.booknote.presentation.add_reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknote.domain.model.Reminder
import com.example.booknote.domain.use_case.ReminderUseCases
import com.example.booknote.domain.use_case.SetReminder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class AddReminderViewModel @Inject constructor(
    private val setReminder: SetReminder,
    private val reminderUseCases: ReminderUseCases
) : ViewModel(){

    private val dayOfWeekMap = mapOf(
        "Mon" to DayOfWeek.MONDAY,
        "Tue" to DayOfWeek.TUESDAY,
        "Wed" to DayOfWeek.WEDNESDAY,
        "Thu" to DayOfWeek.THURSDAY,
        "Fri" to DayOfWeek.FRIDAY,
        "Sat" to DayOfWeek.SATURDAY,
        "Sun" to DayOfWeek.SUNDAY
    )

    fun onEvent(event: AddReminderEvent) {
        when(event){
            is AddReminderEvent.SetReminder -> {
                for (dayStr in event.days){
                    val targetDay = dayOfWeekMap[dayStr] ?: continue

                    var alarmDateTime = event.time.with(TemporalAdjusters.nextOrSame(targetDay))
                    if (alarmDateTime.isBefore(LocalDateTime.now())) {
                        alarmDateTime = event.time.with(TemporalAdjusters.next(targetDay))
                    }
                    setReminder(alarmDateTime, event.message)
                }
                viewModelScope.launch {
                    reminderUseCases.addReminder(
                        Reminder(
                            message = event.message,
                            hour = event.time.hour,
                            minute = event.time.minute,
                            days = event.days,
                        )
                    )
                }
            }
        }
    }
}