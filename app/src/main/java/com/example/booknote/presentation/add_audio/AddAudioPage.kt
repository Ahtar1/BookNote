package com.example.booknote.presentation.add_audio

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booknote.domain.model.Note
import com.example.booknote.presentation.add_audio.components.SaveBottomSheet
import com.example.booknote.presentation.util.record.NoteAudioRecorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AddAudioPage(
    navController: NavController,
    bookId: Long,
    viewModel: AddAudioViewModel = hiltViewModel()
) {
    val context = LocalContext.current.applicationContext
    val recorder by remember {
        mutableStateOf(NoteAudioRecorder(context))
    }

    var audioFile by remember { mutableStateOf<File?>(null) }
    var volumeLevel by remember { mutableStateOf(0f) }
    val volumeLevels = remember { mutableStateListOf<Float>() }
    var isRecording by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var continueRecording by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var timer by remember { mutableIntStateOf(0) }

    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(1000)
            timer += 1
        }
    }

    ActivityCompat.requestPermissions(
        LocalContext.current as Activity,
        arrayOf(android.Manifest.permission.RECORD_AUDIO),
        1
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Add Audio")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Time: ${timer / 60}:${(timer % 60).toString().padStart(2, '0')}", // Dakika:saniye formatında göster
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 25.sp)
            )

            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)) {
                var lastXOffset = 0f
                val barWidth = 2.dp.toPx()

                for (i in 0 until size.width.toInt() step 20) {
                    if (i % 100 == 0) {
                        drawLine(
                            color = Color.Gray,
                            start = Offset(x = i.toFloat(), y = 0f),
                            end = Offset(x = i.toFloat(), y = 40f),
                            strokeWidth = 3.dp.toPx()
                        )
                    } else{
                        drawLine(
                            color = Color.Gray,
                            start = Offset(x = i.toFloat(), y = 0f),
                            end = Offset(x = i.toFloat(), y = 20f),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }

                for (i in 0 until size.width.toInt() step 20) {
                    if (i % 100 == 0) {
                        drawLine(
                            color = Color.Gray,
                            start = Offset(x = i.toFloat(), y = size.height),
                            end = Offset(x = i.toFloat(), y = size.height - 40f),
                            strokeWidth = 3.dp.toPx()
                        )
                    } else{
                        drawLine(
                            color = Color.Gray,
                            start = Offset(x = i.toFloat(), y = size.height),
                            end = Offset(x = i.toFloat(), y = size.height - 20f),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }
                volumeLevels.forEachIndexed { index, level ->
                    val rectHeight = size.height * level
                    val xOffset = index * barWidth
                    drawRect(
                        color = Color.Blue,
                        topLeft = Offset(x = index * barWidth, y = size.height - rectHeight - (size.height / 2 - rectHeight / 2)),
                        size = Size(barWidth, rectHeight)
                    )
                    lastXOffset = xOffset
                }
                drawLine(
                    color = Color.Red,
                    start = Offset(x = lastXOffset + barWidth, y = 0f),
                    end = Offset(x = lastXOffset + barWidth, y = size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }

            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (isRecording) {
                            isPaused = true
                            isRecording = false
                            recorder.pause()
                        } else {
                            if(isPaused){
                                if (audioFile != null) {
                                    continueRecording = true
                                    isPaused = false
                                    isRecording = true
                                    recorder.resume()
                                    scope.launch {
                                        while (isRecording) {
                                            volumeLevel = recorder.getVolumeLevel()
                                            volumeLevels.add(volumeLevel)
                                            if (volumeLevels.size > 100) {
                                                volumeLevels.removeAt(0)
                                            }
                                            delay(100)
                                        }
                                    }
                                }
                                recorder.resume()
                                isPaused = false
                                isRecording = true
                            } else{
                                val permanentFile = File(context.getExternalFilesDir(null), "temporalaudio.mp3")
                                isRecording = true
                                continueRecording = false
                                recorder.start(permanentFile)
                                audioFile = permanentFile
                                volumeLevels.clear()
                                scope.launch {
                                    while (isRecording && !isPaused) {
                                        volumeLevel = recorder.getVolumeLevel()
                                        volumeLevels.add(volumeLevel)
                                        if (volumeLevels.size > 100) {
                                            volumeLevels.removeAt(0)
                                        }
                                        delay(100)
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.size(96.dp)
                ) {
                    AnimatedContent(
                        targetState = isRecording,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))
                        },
                        label = ""
                    ) { targetState ->
                        if (targetState) {
                            Icon(
                                imageVector = Icons.Filled.PauseCircle,
                                contentDescription = "Pause",
                                modifier = Modifier.size(80.dp),
                                tint = Color(0xFFD81E15)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Circle,
                                contentDescription = "Record",
                                modifier = Modifier
                                    .size(96.dp)
                                    .padding(12.dp)
                                    .border(1.dp, Color.Black, CircleShape),
                                tint = Color(0xFFD81E15)
                            )
                        }
                    }
                }

                IconButton(
                    modifier = Modifier.size(80.dp),
                    onClick = {
                        if(audioFile != null){
                            isRecording = false
                            isPaused = false
                            recorder.stop()
                            viewModel.onEvent(AddAudioEvent.SaveButtonClicked)
                        } else{
                            Toast.makeText(context, "You haven't recorded any voice yet!", Toast.LENGTH_SHORT).show()
                        }
                }) {
                    Icon(
                        modifier = Modifier.size(80.dp),
                        imageVector = Icons.Filled.StopCircle,
                        tint = Color(0xFFD81E15),
                        contentDescription = "Save"
                    )
                }
            }

            if (viewModel.isBottomSheetShown){
                SaveBottomSheet(
                    onDismissRequest = { viewModel.onEvent(AddAudioEvent.DismissBottomSheet) },
                    onSave = { title, pageNumber ->
                        val finalFile = File(context.getExternalFilesDir(null), "audio_${bookId}_${title}.mp3")
                        val isSuccessfullyRenamed = audioFile?.renameTo(finalFile)
                        audioFile = finalFile

                        if (isSuccessfullyRenamed == true){
                            if (audioFile?.exists() == true) {
                                viewModel.onEvent(
                                    AddAudioEvent.AddAudio(
                                        Note(
                                            noteTitle = title,
                                            audioFilePath = audioFile?.absolutePath,
                                            page = pageNumber.toInt(),
                                            dateCreated = LocalDateTime.now().format(
                                                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                                            ),
                                            bookId = bookId
                                        )
                                    )
                                )
                            }
                        }
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}

