package com.example.booknote.presentation.calendar

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booknote.presentation.util.record.NoteAudioPlayer
import io.github.boguszpawlowski.composecalendar.CalendarState
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.day.Day
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import java.io.File
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarPage(
    navController: NavController,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calendar",
                        style = TextStyle(fontSize = 20.sp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
            )
        },
    ) {
        val context = LocalContext.current.applicationContext
        val player by lazy {
            NoteAudioPlayer(context)
        }

        val calendarState = rememberSelectableCalendarState(
            initialSelection = listOf(LocalDate.now()),
            initialSelectionMode = SelectionMode.Single
        )

        Column {
            SelectableCalendar(
                modifier = Modifier
                    .padding(it),
                dayContent = { day ->
                    DayContent(day = day, viewModel.dates, calendarState, viewModel)
                },
                calendarState = calendarState
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {

                item {
                    Text(
                        text = "Notes: ${viewModel.notes.value.size}",
                        style = TextStyle(fontSize = 20.sp)
                    )
                }

                items(viewModel.notes.value){ note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xffD8EFD3)
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(0.9f)
                                    .padding(16.dp)
                                    .padding(end = 32.dp)
                            ) {
                                Text(
                                    text = note.noteTitle,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 10,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (note.noteText != null) {
                                    Text(
                                        text = note.noteText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 10,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                if (note.audioFilePath != null) {
                                    ElevatedButton(onClick = {
                                        val audioFile = File(note.audioFilePath)
                                        player.playFile(audioFile)
                                    }) {
                                        Text(text = "Play Audio Note")
                                    }
                                }
                                note.imageFilePath?.let { imagePath ->
                                    val imageFile = File(imagePath)
                                    if (imageFile.exists()) {
                                        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(300.dp)
                                                .padding(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }


    }
}

@Composable
fun DayContent(day: Day, dateList: List<LocalDate>, calendarState: CalendarState<DynamicSelectionState>, viewModel: CalendarViewModel) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clickable {
                calendarState.selectionState.selection = listOf(day.date)
                viewModel.onEvent(CalendarEvent.GetNotes(day.date))
            },
        colors = CardDefaults.cardColors(
            containerColor = if( dateList.contains(day.date) ) Color(0xFFdbead5) else Color.White,
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        border = if(day.isFromCurrentMonth) BorderStroke(1.dp, Color(0xFF686868)) else BorderStroke(1.dp, Color(0xFFdbead5)),
        ){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier,
                    text = day.date.dayOfMonth.toString(),
                    textAlign = TextAlign.Center
                )
            }
    }
}