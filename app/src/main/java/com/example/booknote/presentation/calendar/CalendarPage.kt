package com.example.booknote.presentation.calendar

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.booknote.R
import com.example.booknote.presentation.util.Page
import com.example.booknote.presentation.util.extension.noRippleClickable
import com.example.booknote.presentation.util.record.ExoPlayer
import com.mohamedrejeb.richeditor.ui.material3.RichText
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
        val calendarState = rememberSelectableCalendarState(
            initialSelection = listOf(LocalDate.now()),
            initialSelectionMode = SelectionMode.Single
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            item {
                SelectableCalendar(
                    modifier = Modifier
                        .padding(it),
                    dayContent = { day ->
                        DayContent(day = day, viewModel.dates, calendarState, viewModel)
                    },
                    calendarState = calendarState
                )
            }

            item{
                Text(
                    text = stringResource(R.string.focus_sessions),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                )
            }

            if (viewModel.focusSessions.value.isNotEmpty()){
                items(viewModel.focusSessions.value){ focusSession ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (focusSession.bookId == null) stringResource(R.string.undefined_book) else viewModel.getBookNameById(focusSession.bookId),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = viewModel.formatDuration(focusSession.duration),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            } else{
                item{
                    Text(
                        text = stringResource(R.string.no_focus_session),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                    )
                }
            }

            item{
                Text(
                    text = stringResource(R.string.notes),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                )
            }

            if (viewModel.notes.value.isEmpty()){
                item {
                    Text(
                        text = stringResource(R.string.no_notes),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                    )
                }
            } else{
                items(viewModel.notes.value){ note ->
                    val richTextState = viewModel.getRichTextState(note)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .noRippleClickable {
                                if (note.audioFilePath == null) {
                                    navController.navigate(
                                        Page.AddNotePage.route + "?bookId=${note.bookId}&noteId=${note.id}"
                                    )
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(note.color)
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
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
                                    RichText(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        state = richTextState,
                                        lineHeight = 36.sp,
                                    )
                                }
                                if (note.audioFilePath != null) {
                                    ExoPlayer(Uri.fromFile(File(note.audioFilePath)))
                                }
                                note.imageFilePath?.let { imagePath ->
                                    val imageFile = File(imagePath)
                                    if (imageFile.exists()) {
                                        Image(
                                            painter = rememberAsyncImagePainter(model = imageFile),
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
                viewModel.onEvent(CalendarEvent.GetFocusSessions(day.date))
            },
        colors = CardDefaults.cardColors(
            containerColor = if( dateList.contains(day.date) ) Color(0xFFdbead5) else Color.White,
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        border = if(calendarState.selectionState.isDateSelected(day.date)) BorderStroke(2.dp, Color(0xFF686868)) else if(day.isFromCurrentMonth) BorderStroke(1.dp, Color(0xFF686868)) else BorderStroke(1.dp, Color(0xFFdbead5)),
        ){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                if (LocalDate.now() == day.date) {
                    Canvas(modifier = Modifier) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        val path = Path().apply {
                            moveTo(canvasWidth / 2f, 0f)
                            lineTo(0f, canvasHeight)
                            lineTo(canvasWidth, canvasHeight)
                            close()
                        }

                        drawPath(
                            path = path,
                            color = Color.Red,
                            style = androidx.compose.ui.graphics.drawscope.Fill
                        )
                    }
                }
                Text(
                    modifier = Modifier,
                    text = day.date.dayOfMonth.toString(),
                    textAlign = TextAlign.Center
                )
            }
    }
}