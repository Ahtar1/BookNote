package com.example.booknote.presentation.add_audio

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booknote.domain.model.Note
import com.example.booknote.presentation.util.record.NoteAudioPlayer
import com.example.booknote.presentation.util.record.NoteAudioRecorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAudioPage(
    navController: NavController,
    bookId: Long,
    viewModel: AddAudioViewModel = hiltViewModel()
) {
    val context = LocalContext.current.applicationContext
    val recorder by lazy {
        NoteAudioRecorder(context)
    }

    val player by lazy {
        NoteAudioPlayer(context)
    }

    var audioFile: File? = null
    var volumeLevel by remember { mutableStateOf(0f) }
    val volumeLevels = remember { mutableStateListOf<Float>() }
    var isRecording by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var continueRecording by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                })
        },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            var title by remember {
                mutableStateOf("")
            }
            var pageNumber by remember {
                mutableStateOf("")
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                value = title,
                onValueChange = {title = it},
                label = { Text("Title") },
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                maxLines = 1,
                label = { Text("Page Number") },
                value = pageNumber,
                onValueChange = { newText ->
                    if (newText.all { it.isDigit() }) {
                        pageNumber = newText
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 25.sp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
            )
            if (isPaused) {
                ElevatedButton(onClick ={
                    recorder.resume()
                    isPaused = false
                    isRecording = true
                }) {
                    Text(text ="Resume recording")
                }
            } else {
                ElevatedButton(
                    onClick = {
                        val permanentFile = File(context.getExternalFilesDir(null), "audio_${bookId}_${title}.mp3")
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
                ) {
                    Text(text = "Start Recording")
                }
            }

            ElevatedButton(onClick = {
                isRecording = false
                recorder.stop()
            }) {
                Text(text = "Stop recording")
            }

            ElevatedButton(
                onClick = {
                    recorder.pause()
                    isPaused = true
                },
                enabled = isRecording
            ) {
                Text(text = "Pause recording")
            }

            ElevatedButton(onClick = {
                if (audioFile != null) {
                    isRecording = true
                    continueRecording = true
                    recorder.start(audioFile!!)
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
            }) {
                Text(text = "Continue recording")
            }

            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)) {
                var lastXOffset = 0f
                val barWidth = 2.dp.toPx()
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

            ElevatedButton(onClick = {
                player.playFile(audioFile ?: return@ElevatedButton)
            }) {
                Text(text = "Play")
            }
            ElevatedButton(onClick = {
                player.stop()
            }) {
                Text(text = "Stop playing")
            }

            ElevatedButton(onClick = {
                if (audioFile?.exists() == true) {
                    viewModel.onEvent(
                        AddAudioEvent.AddAudio(
                            Note(
                                noteTitle = title,
                                audioFilePath = audioFile?.absolutePath,
                                page = pageNumber.toIntOrNull() ?: 0,
                                dateCreated = LocalDateTime.now().format(
                                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                                ),
                                bookId = bookId
                            )
                        )
                    )
                    println("Recording saved: ${audioFile?.absolutePath}")
                } else {
                    println("Recording failed")
                }
            }) {
                Text(text = "Save")
            }
        }
    }
}