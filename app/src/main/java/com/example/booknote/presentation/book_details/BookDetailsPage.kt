package com.example.booknote.presentation.book_details

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.booknote.domain.model.Book
import com.example.booknote.presentation.add_book.components.AddBookImageBottomSheet
import com.example.booknote.presentation.add_book.createImageUri
import com.example.booknote.presentation.notes.saveImageToInternalStorage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsPage(
    navController: NavController,
    bookId: Long,
    viewModel: BookDetailsViewModel = hiltViewModel()
){

    val state = viewModel.state.value

    var bookTitleText by remember { mutableStateOf("") }
    var bookAuthor by remember { mutableStateOf("") }
    var bookPublisher by remember { mutableStateOf("") }
    var bookLanguage by remember { mutableStateOf("") }
    var bookStatus by remember { mutableStateOf(Book.BookStatus.TO_READ) }
    var expanded by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    var imagePath by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    val cameraUri = remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            cameraUri.value?.let { uri ->
                val imageFile = saveImageToInternalStorage(context, uri)
                imagePath = imageFile?.absolutePath ?: ""
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val imageFile = saveImageToInternalStorage(context, uri)
            imagePath = imageFile?.absolutePath ?: ""

        }

    }

    LaunchedEffect(Unit){
        viewModel.onEvent(BookDetailsEvent.GetBookDetails(bookId))
    }

    LaunchedEffect(imagePath) {
        if (imagePath.isNotEmpty()) {
            viewModel.onEvent(BookDetailsEvent.UpdateBookImage(imagePath))
        }
    }

    LaunchedEffect(state) {
        state.let { book ->
            bookTitleText = book.title
            bookAuthor = book.author
            bookPublisher = book.publisher
            bookLanguage = book.language
            bookStatus = book.status
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Book") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.onEvent(
                            BookDetailsEvent.UpdateBook(
                                Book(
                                    id = bookId,
                                    title = bookTitleText,
                                    author = bookAuthor,
                                    publisher = bookPublisher,
                                    language = bookLanguage,
                                    status = bookStatus,
                                    bookImagePath = imagePath,
                                )
                            )
                        )
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("refresh", true)
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        },
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = it)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item{
                if (state.bookImageFilePath != ""){
                    AsyncImage(
                        model = state.bookImageFilePath,
                        contentDescription = null,
                        modifier = Modifier
                            .height(200.dp)
                            .width(150.dp)
                            .padding(top = 10.dp)
                            .clickable {
                                showBottomSheet = true
                            },
                    )
                } else {
                    Card(
                        modifier = Modifier
                            .height(200.dp)
                            .width(150.dp)
                            .padding(top = 10.dp)
                            .clickable {
                                showBottomSheet = true
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardColors(
                            containerColor = Color.Gray,
                            contentColor = Color.Gray,
                            disabledContentColor = Color.Gray,
                            disabledContainerColor = Color.Gray,
                        ),
                    ){
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Book Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(align = Alignment.Center),
                            tint = Color.Black
                        )
                    }
                }
            }
            item{
                Text(
                    text = "Title",
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                )
            }

            item {
                OutlinedTextField(
                    value = bookTitleText,
                    onValueChange = { newText ->
                        bookTitleText = newText
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color.Black,
                        focusedContainerColor = Transparent,
                        unfocusedContainerColor = Transparent,
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .padding(top = 10.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
            }

            item {
                Text(
                    text = "Author",
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                )
            }

            item {
                OutlinedTextField(
                    value = bookAuthor,
                    onValueChange = { newText ->
                        bookAuthor = newText
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color.Black,
                        focusedContainerColor = Transparent,
                        unfocusedContainerColor = Transparent,
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
            }

            item {
                Text(
                    text = "Publisher",
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                )
            }

            item {
                OutlinedTextField(
                    value = bookPublisher,
                    onValueChange = { newText ->
                        bookPublisher = newText
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color.Black,
                        focusedContainerColor = Transparent,
                        unfocusedContainerColor = Transparent,
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
            }

            item {
                Text(
                    text = "Language",
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                )
            }

            item {
                OutlinedTextField(
                    value = bookLanguage,
                    onValueChange = { newText ->
                        bookLanguage = newText
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color.Black,
                        focusedContainerColor = Transparent,
                        unfocusedContainerColor = Transparent,
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.clearFocus() }
                    )
                )
            }

            item {
                Text(
                    text = "Status",
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                )
            }

            item {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = bookStatus.name,
                        onValueChange = {  },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                    ) {
                        Book.BookStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.name) },
                                onClick = {
                                    bookStatus = status
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

        }
        if (showBottomSheet) {
            AddBookImageBottomSheet(
                onCameraClick = {
                    val uri = createImageUri(context)
                    cameraUri.value = uri
                    cameraLauncher.launch(uri)
                },
                onGalleryClick = {
                    galleryLauncher.launch("image/*")
                },
                onDismiss = { showBottomSheet = false }
            )
        }
    }
}