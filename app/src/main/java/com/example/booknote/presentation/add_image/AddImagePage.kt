package com.example.booknote.presentation.add_image

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booknote.domain.model.Note
import com.example.booknote.presentation.notes.saveImageToInternalStorage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddImagePage(
    navController: NavController,
    bookId: Long,
    viewModel: AddImageViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Add Note")
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

        val context = LocalContext.current.applicationContext

        var title by remember {
            mutableStateOf("")
        }
        var pageNumber by remember {
            mutableStateOf("")
        }

        var imagePath by remember {
            mutableStateOf("")
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                val imageFile = saveImageToInternalStorage(context, uri)
                imagePath = imageFile?.absolutePath ?: ""
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp), // Added padding for overall content
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val focusRequester = remember { FocusRequester() }
            val focusManager = LocalFocusManager.current

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            OutlinedTextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            ElevatedButton(
                onClick = {
                    launcher.launch("image/*")
                }
            ) {
                Text(text = "Add Image")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (imagePath != ""){
                Image(
                    bitmap = BitmapFactory.decodeFile(imagePath).asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(300.dp)
                        .padding(10.dp)
                )
            }

            ElevatedButton(
                onClick = {
                    viewModel.onEvent(
                        AddImageEvent.AddImage(
                            Note(
                                noteTitle = title,
                                imageFilePath = imagePath,
                                page = pageNumber.toIntOrNull() ?: 0,
                                dateCreated = LocalDateTime.now().format(
                                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                                ),
                                bookId = bookId

                            )
                        )
                    )
                    navController.navigateUp()
                }
            ) {
                Text(text ="Kaydet")
            }
        }
    }
}
