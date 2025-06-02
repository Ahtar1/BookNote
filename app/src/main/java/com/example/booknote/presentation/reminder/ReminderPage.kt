package com.example.booknote.presentation.reminder


import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booknote.MainActivity
import com.example.booknote.presentation.util.Page

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderPage(
    navController: NavController,
    viewModel: ReminderViewModel = hiltViewModel()
){

    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reminders") },
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
                            navController.navigate(Page.AddReminderPage.route)
                        },
                        modifier = Modifier
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Reminder",
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                }
            )
        }
    ){
        if (Build.VERSION.SDK_INT> 33){
            ActivityCompat.requestPermissions(
                (navController.context as MainActivity),
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                2
            )
        }

        LazyColumn(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            if (state.value.reminders.isEmpty()) {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxSize(),
                        text = "No reminders set",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
            items(state.value.reminders) { reminder ->
                var isSwitchChecked by remember { mutableStateOf(reminder.isActive) }
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = reminder.message,
                            modifier = Modifier
                                .padding(top = 16.dp, start = 16.dp, bottom = 8.dp, end = 16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )
                        IconButton(
                            onClick = {
                                viewModel.onEvent(ReminderEvent.DeleteReminder(reminder))
                            },
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Delete Reminder",
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = reminder.hour.toString() + ":" +
                                    reminder.minute.toString().padStart(2, '0'),
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = reminder.days.joinToString(","),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                            Switch(
                                checked = isSwitchChecked,
                                onCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        viewModel.onEvent(
                                            ReminderEvent.ChangeReminderStatus(reminder)
                                        )
                                        isSwitchChecked = true
                                    } else {
                                        viewModel.onEvent(
                                            ReminderEvent.ChangeReminderStatus(reminder)
                                        )
                                        isSwitchChecked = false
                                    }
                                },
                                modifier = Modifier
                            )
                        }
                    }

                }
            }
        }
    }
}