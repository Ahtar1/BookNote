package com.example.booknote.presentation.books

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.booknote.R
import com.example.booknote.domain.model.Book
import com.example.booknote.domain.util.BooksSortOrder
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

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var selectedBottomSheetItem by remember { mutableStateOf<ToggleItem?>(ToggleItem(1, "Book Title Ascending", BooksSortOrder.BookTitleAsc),) }
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.7f)
                    .padding(top = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))
                    Text("Başlık", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider()

                    Text("Section 1", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                    NavigationDrawerItem(
                        label = { Text("VIP Ol") },
                        selected = false,
                        onClick = { /* Handle click */ }
                    )
                    NavigationDrawerItem(
                        label = { Text("Favori Notlar") },
                        selected = false,
                        onClick = { /* Handle click */ }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text("Section 2", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                    NavigationDrawerItem(
                        label = { Text("Karanlık Mod") },
                        selected = false,
                        badge = {

                        },
                        onClick = { /* Handle click */ },
                    )
                    NavigationDrawerItem(
                        label = { Text("Settings") },
                        selected = false,
                        icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                        onClick = { /* Handle click */ }
                    )
                    NavigationDrawerItem(
                        label = { Text("Help and feedback") },
                        selected = false,
                        icon = { Icon(Icons.AutoMirrored.Outlined.Help, contentDescription = null) },
                        onClick = { /* Handle click */ },
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        },
        drawerState = drawerState

    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    modifier = Modifier
                        .fillMaxWidth(),
                    title = {
                        Text(text = if (selectionMode) "${selectedBooks.size} Seçildi" else "Kitaplarım")
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
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
                        } else{
                            IconButton(onClick = {
                                navController.navigate(Page.CalendarPage.route)
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarMonth,
                                    contentDescription = "Calendar"
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
                        navController.navigate(
                            Page.AddBookPage.route +
                                    "?bookId=-1"
                        )
                    },
                    content = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                var query by remember { mutableStateOf("") }
                var active by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchBar(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(),
                        query = query,
                        onQueryChange = {
                            active = true
                            query = it
                            searchJob?.cancel()
                            searchJob = scope.launch {
                                delay(500)
                                viewModel.onEvent(BooksEvent.GetBooks(query, state.order))
                            }
                        },
                        onSearch = { newQuery ->
                            active = false
                            viewModel.onEvent(BooksEvent.GetBooks(newQuery, state.order))
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
                                                viewModel.onEvent(BooksEvent.GetBooks(query, state.order))
                                            } else{
                                                active = false
                                            }
                                        },
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close Icon",
                                )
                            }
                        },
                        content = {}
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    IconButton(
                        modifier = Modifier.size(34.dp),
                        onClick = {
                            viewModel.onEvent(BooksEvent.OrderButtonClicked)
                        }) {
                        Icon(
                            modifier = Modifier.size(34.dp),
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Sort"
                        )
                    }
                }

                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    columns = GridCells.Fixed(2),
                    content = {
                        items(
                            items= state.books,
                            key = { book -> book.id },
                        ) { book ->
                            Column(
                                modifier = Modifier
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onLongPress = {
                                                    selectionMode = true
                                                    if (selectedBooks.contains(book)) {
                                                        selectedBooks = selectedBooks - book
                                                        if (selectedBooks.isEmpty()) {
                                                            selectionMode = false
                                                        }
                                                    } else {
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
                                                                    "?bookId=${book.id}"
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                ) {
                                    if(book.bookImagePath.isNotEmpty()){
                                        AsyncImage(
                                            modifier = Modifier
                                                .size(200.dp)
                                                .padding(12.dp),
                                            model = book.bookImagePath,
                                            contentDescription = "Books Grid",
                                        )
                                    } else
                                        Image(
                                            modifier = Modifier.size(200.dp),
                                            painter = painterResource(id = R.drawable.blue_book),
                                            contentDescription = "Books Grid",
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

                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = Color(0xffd5f5e3),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .wrapContentSize()
                                            .align(Alignment.TopEnd),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = book.status.name,
                                            fontSize = 20.sp,
                                            modifier = Modifier
                                                .padding(8.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = book.title,
                                    fontSize = 20.sp,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    },
                )
            }

            if (viewModel.isBottomSheetShown) {
                SortBottomSheet(
                    onDismissRequest = { viewModel.onEvent(BooksEvent.DismissBottomSheet) },
                    sortItems = listOf(
                        ToggleItem(0, "Book Title Ascending", BooksSortOrder.BookTitleAsc),
                        ToggleItem(1, "Book Title Descending", BooksSortOrder.BookTitleDesc),
                        ToggleItem(2, "Author Ascending", BooksSortOrder.AuthorAsc),
                        ToggleItem(3, "Author Descending", BooksSortOrder.AuthorDesc),
                        ToggleItem(4, "Language Ascending", BooksSortOrder.LanguageAsc),
                        ToggleItem(5, "Language Descending", BooksSortOrder.LanguageDesc),
                    ),
                    initialSelectedItem = selectedBottomSheetItem,
                    onItemSelected = { newItem -> selectedBottomSheetItem = newItem },
                    onClick = { order ->
                        viewModel.onEvent(BooksEvent.ChangeOrder(order))
                        viewModel.onEvent(BooksEvent.GetBooks(state.searchQuery, order))
                        viewModel.onEvent(BooksEvent.DismissBottomSheet)
                    }
                )
            }
        }
    }
}
