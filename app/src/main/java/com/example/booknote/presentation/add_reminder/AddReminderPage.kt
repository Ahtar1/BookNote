package com.example.booknote.presentation.add_reminder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderPage(
    navController: NavController,
    viewModel: AddReminderViewModel = hiltViewModel()
){
    val context = LocalContext.current

    var selectedDaysList by remember {
        mutableStateOf(
            emptyList<String>()
        )
    }

    var title by remember {
        mutableStateOf("")
    }

    val timePickerState = rememberTimePickerState(
        initialHour = 12,
        initialMinute = 0,
        is24Hour = true
    )

    val baseTime = LocalDateTime.now()
        .withHour(timePickerState.hour)
        .withMinute(timePickerState.minute)
        .withSecond(0)
        .withNano(0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Reminder") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (ActivityCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    Toast
                                        .makeText(
                                            context,
                                            "Please allow notification permission",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else{
                                    viewModel.onEvent(
                                        AddReminderEvent.SetReminder(
                                            time = baseTime,
                                            days = selectedDaysList,
                                            message = title
                                        )
                                    )
                                    navController.navigateUp()
                                }
                            } else{
                                viewModel.onEvent(
                                    AddReminderEvent.SetReminder(
                                        time = baseTime,
                                        days = selectedDaysList,
                                        message = title
                                    )
                                )
                                navController.navigateUp()
                            }
                        },
                        modifier = Modifier
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Add Reminder",
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                }
            )
        }
    ){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            item {
                TimePicker(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    state = timePickerState,
                    colors = TimePickerDefaults.colors().copy(
                        timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.secondary,
                        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                    )
                )
            }

            item {
                LazyRow(
                    modifier = Modifier
                        .wrapContentSize()
                ) {
                    items(
                        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    ){
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(percent = 50))
                                .background(color = if (selectedDaysList.contains(it)) MaterialTheme.colorScheme.primary else Color.LightGray)
                                .clickable {
                                    if (selectedDaysList.contains(it)) {
                                        selectedDaysList -= it
                                    } else {
                                        selectedDaysList += it
                                    }
                                }
                        ) {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .wrapContentSize()
                                    .align(Alignment.Center),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    singleLine = true,
                    label = { Text("Reminder Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}
