package com.example.booknote.presentation.books

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booknote.R
import com.example.booknote.domain.model.Book
import com.example.booknote.presentation.books.components.AddBookDialog
import com.example.booknote.presentation.util.Page
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksPage(
    navController: NavController,
    viewModel: BooksViewModel = hiltViewModel()
){

    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var searchJob by remember { mutableStateOf<Job?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = {
                Text(text = "Kitaplarım")
            })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                          viewModel.onEvent(BooksEvent.AddBookButtonClicked)
                },
                shape = CircleShape,
                content = { Icon(Icons.Filled.Add, contentDescription = "Add") },
            )
        },
    ) { paddingValues ->
        paddingValues
        Column(
            modifier = Modifier.padding(top = 56.dp),
        ) {
            var query by remember { mutableStateOf("") }
            var active by remember { mutableStateOf(false) }
            Row {
                SearchBar(
                    modifier = Modifier.padding(start = 16.dp),
                    query = query,
                    onQueryChange = {
                        query = it
                        searchJob?.cancel()
                        searchJob = scope.launch {
                            delay(500)
                            viewModel.onEvent(BooksEvent.GetBooks(query))
                        }
                                    },
                    onSearch = { newQuery ->
                        active = false
                        viewModel.onEvent(BooksEvent.GetBooks(newQuery))
                    },
                    active = false,
                    onActiveChange = { active = it },
                    placeholder = {
                        Text(text = "Search")
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "searchIcon")
                    },
                    trailingIcon = {
                        if (active){
                            Icon(
                                modifier = Modifier
                                    .clickable {
                                        if (query.isNotEmpty()) {
                                            query = ""
                                        } else{
                                            active = false
                                        }

                                    },
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close Icon",)
                        }
                    }
                ){}
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Sort Icon",
                )
            }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    content = {
                        items(state.books) { book ->
                            Box(
                                modifier = Modifier.clickable {
                                    navController.navigate(
                                        Page.NotesPage.route +
                                                "?bookId=${book.id}&bookTitle=${book.title}"
                                    )
                                }
                            ) {
                                Image(
                                    modifier = Modifier.size(200.dp),
                                    painter = painterResource(id = R.drawable.blue_book),
                                    contentDescription = "Books Grid",
                                )
                                Text(
                                    text = book.title,
                                    modifier = Modifier.align(Alignment.Center)
                                    )
                                Button(
                                    onClick = {
                                        viewModel.onEvent(BooksEvent.DeleteBook(book))
                                        scope.launch {
                                            val result = snackbarHostState.showSnackbar(
                                                message = "Note deleted",
                                                actionLabel = "Undo"
                                            )
                                            if(result == SnackbarResult.ActionPerformed) {
                                                viewModel.onEvent(BooksEvent.RestoreBook)
                                            }
                                        }
                                    }
                                ) {
                                    Text(text = "Delete")
                                    Spacer(modifier = Modifier.size(2.dp))
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete Book Icon")
                                }
                            }
                        }
                    },
                )

        }
        if (viewModel.isDialogShown){
            AddBookDialog(
                onDismiss = {
                    viewModel.onEvent(BooksEvent.DismissDialog)
                            },
                onConfirm = { book ->
                    viewModel.onEvent(BooksEvent.AddBook(
                        Book(
                            title = book.title,
                            author = book.author,
                            publisher = book.publisher,
                            language = book.language
                        )
                    ))
                    viewModel.onEvent(BooksEvent.DismissDialog)
                }
            )
        }
    }
}