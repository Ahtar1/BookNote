package com.example.booknote.presentation.book_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booknote.domain.model.Book

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

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit){
        viewModel.onEvent(BookDetailsEvent.GetBookDetails(bookId))
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
                                    status = bookStatus
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = it)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Title",
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
            )

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

            Text(
                text = "Author",
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
            )

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

            Text(
                text = "Publisher",
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
            )

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

            Text(
                text = "Language",
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
            )

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

            Text(
                text = "Status",
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
            )

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
}