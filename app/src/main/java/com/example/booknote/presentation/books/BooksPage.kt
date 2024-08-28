package com.example.booknote.presentation.books

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booknote.R
import com.example.booknote.domain.model.Book
import com.example.booknote.domain.util.NotesSortOrder
import com.example.booknote.presentation.books.components.AddBookDialog
import com.example.booknote.presentation.notes.components.SortBottomSheet
import com.example.booknote.presentation.notes.components.ToggleItem
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
    var selectionMode by remember { mutableStateOf(false) }
    var selectedBooks by remember { mutableStateOf(listOf<Book>()) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = if (selectionMode) "${selectedBooks.size} Seçildi" else "Kitaplarım")
                },
                actions = {
                    if (selectionMode) {
                        IconButton(onClick = {
                            viewModel.onEvent(BooksEvent.DeleteBook(selectedBooks))
                            selectionMode = false
                            selectedBooks = emptyList()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Selected Books"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = Color(0xff54b7de),
                onClick = {
                    viewModel.onEvent(BooksEvent.AddBookButtonClicked)
                },
                content = { Icon(Icons.Filled.Add, contentDescription = "Add") },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {
            var query by remember { mutableStateOf("") }
            var active by remember { mutableStateOf(false) }
            Row {
                SearchBar(
                    modifier = Modifier,
                    query = query,
                    onQueryChange = {
                        active = true
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
                                            active = false
                                            viewModel.onEvent(BooksEvent.GetBooks(query))
                                        } else{
                                            active = false
                                        }
                                    },
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close Icon",
                            )
                        }
                    }
                ){
                    Text(text = "Search")
                }
                IconButton(
                    onClick = {
                        viewModel.onEvent(BooksEvent.OrderButtonClicked)
                    }) {
                    Icon(
                        imageVector = Icons.Filled.Sort,
                        contentDescription = "Sort"
                    )
                }
            }

            LazyVerticalGrid(
                modifier = Modifier
                    .padding(top = 16.dp),
                columns = GridCells.Fixed(2),
                content = {
                    items(state.books) { book ->
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            selectionMode = true
                                            if (selectedBooks.contains(book)) {
                                                // Kitap seçiliyse, seçimi kaldır
                                                selectedBooks = selectedBooks - book
                                                if (selectedBooks.isEmpty()) {
                                                    selectionMode = false
                                                }
                                            } else {
                                                // Kitap seçili değilse, seçimi ekle
                                                selectedBooks = selectedBooks + book
                                            }
                                        },
                                        onTap = {
                                            if(selectionMode){
                                                if (selectedBooks.contains(book)) {
                                                    selectedBooks = selectedBooks - book
                                                    if (selectedBooks.isEmpty()) {
                                                        selectionMode = false
                                                    }
                                                } else {
                                                    selectedBooks = selectedBooks + book
                                                }
                                            } else{
                                                navController.navigate(
                                                    Page.NotesPage.route +
                                                            "?bookId=${book.id}&bookTitle=${book.title}"
                                                )
                                            }
                                        }
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

                            if (selectionMode && selectedBooks.contains(book)) {
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center),
                                    onDraw = {
                                        drawRect(
                                            color = Color(0x80000000), // Semi-transparent overlay
                                            size = size
                                        )
                                    }
                                )
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                },
            )
        }

        if (viewModel.isBottomSheetShown) {
            SortBottomSheet(
                onDismissRequest = { viewModel.onEvent(BooksEvent.DismissBottomSheet) },
                sortItems = listOf(
                    ToggleItem(0, "Note Title Ascending", NotesSortOrder.NoteTitleAsc),
                    ToggleItem(1, "Note Title Descending", NotesSortOrder.NoteTitleDesc),
                    ToggleItem(2, "Page Ascending", NotesSortOrder.PageAsc),
                    ToggleItem(3, "Page Descending", NotesSortOrder.PageDesc)
                ),
                onClick = { order ->
                    viewModel.onEvent(BooksEvent.ChangeOrder(order)); viewModel.onEvent(BooksEvent.DismissBottomSheet)
                }
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
