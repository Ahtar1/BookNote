package com.example.booknote.presentation.reminder

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknote.domain.use_case.CancelReminder
import com.example.booknote.domain.use_case.ReminderUseCases
import com.example.booknote.domain.use_case.SetReminder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderUseCases: ReminderUseCases,
    private val setReminder: SetReminder,
    private val cancelReminder: CancelReminder
): ViewModel() {

    private val _state = mutableStateOf(ReminderState())
    var state: State<ReminderState> = _state

    val dayOfWeekMap = mapOf(
        "Mon" to DayOfWeek.MONDAY,
        "Tue" to DayOfWeek.TUESDAY,
        "Wed" to DayOfWeek.WEDNESDAY,
        "Thu" to DayOfWeek.THURSDAY,
        "Fri" to DayOfWeek.FRIDAY,
        "Sat" to DayOfWeek.SATURDAY,
        "Sun" to DayOfWeek.SUNDAY
    )

    init {
        viewModelScope.launch {
            reminderUseCases.getReminders().collect { reminders ->
                _state.value = state.value.copy(reminders = reminders)
            }
        }
    }

    fun onEvent(event: ReminderEvent) {
        when (event) {
            is ReminderEvent.ChangeReminderStatus -> {
                viewModelScope.launch {
                    val updatedReminder = event.reminder.copy(isActive = !event.reminder.isActive)
                    reminderUseCases.updateReminder(updatedReminder)
                    _state.value = state.value.copy(reminders = state.value.reminders.map {
                        if (it.id == event.reminder.id) updatedReminder else it
                    })

                    if (updatedReminder.isActive) {
                        val baseTime = LocalDateTime.now()
                            .withHour(updatedReminder.hour)
                            .withMinute(updatedReminder.minute)
                            .withSecond(0)
                            .withNano(0)

                        for (dayStr in event.reminder.days) {
                            val targetDay = dayOfWeekMap[dayStr] ?: continue

                            var alarmDateTime = baseTime.with(TemporalAdjusters.nextOrSame(targetDay))
                            if (alarmDateTime.isBefore(LocalDateTime.now())) {
                                alarmDateTime = baseTime.with(TemporalAdjusters.next(targetDay))
                            }

                            setReminder(alarmDateTime, updatedReminder.message)
                        }
                    } else {
                        val baseTime = LocalDateTime.now()
                            .withHour(updatedReminder.hour)
                            .withMinute(updatedReminder.minute)
                            .withSecond(0)
                            .withNano(0)

                        for (dayStr in event.reminder.days) {
                            val targetDay = dayOfWeekMap[dayStr] ?: continue

                            var alarmDateTime = baseTime.with(TemporalAdjusters.nextOrSame(targetDay))
                            if (alarmDateTime.isBefore(LocalDateTime.now())) {
                                alarmDateTime = baseTime.with(TemporalAdjusters.next(targetDay))
                            }

                            cancelReminder(alarmDateTime, updatedReminder.message)
                        }

                    }
                }
            }
            is ReminderEvent.DeleteReminder -> {
                _state.value = state.value.copy(reminders = state.value.reminders.filter { it.id != event.reminder.id })
                viewModelScope.launch {
                    reminderUseCases.deleteReminder(event.reminder)
                    val baseTime = LocalDateTime.now()
                        .withHour(event.reminder.hour)
                        .withMinute(event.reminder.minute)
                        .withSecond(0)
                        .withNano(0)
                    for (dayStr in event.reminder.days) {
                        val targetDay = dayOfWeekMap[dayStr] ?: continue

                        var alarmDateTime = baseTime.with(TemporalAdjusters.nextOrSame(targetDay))
                        if (alarmDateTime.isBefore(LocalDateTime.now())) {
                            alarmDateTime = baseTime.with(TemporalAdjusters.next(targetDay))
                        }

                        cancelReminder(alarmDateTime, event.reminder.message)
                    }
                }
            }
        }
    }
}