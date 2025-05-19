package com.example.booknote.presentation.focus

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.booknote.R
import com.example.booknote.domain.model.Book
import com.example.booknote.domain.model.FocusSession
import com.example.booknote.presentation.focus.components.AnimatedTimer
import com.example.booknote.presentation.util.Page
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusPage(
    navController: NavController,
    viewModel: FocusViewModel = hiltViewModel()
){
    var isBottomSheetOpened by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onEvent(FocusEvent.GetBooks)
    }
    val state = viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Focus") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.onEvent(FocusEvent.SaveFocus(
                                FocusSession(
                                    duration = state.value.elapsedSeconds,
                                    date = LocalDateTime.now().format(
                                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                                    ),
                                    bookId = if(state.value.selectedBook.id != -1L) state.value.selectedBook.id else null,
                                )
                            ))
                            navController.navigateUp()
                        },
                        modifier = Modifier
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = "Save Focus Session",
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                AnimatedTimer(
                    elapsedSeconds = state.value.elapsedSeconds,
                    isRunning = state.value.isRunning,
                    onTick = {  }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Button({
                        if (state.value.isRunning) {
                            viewModel.onEvent(FocusEvent.StopTimer)
                        } else {
                            viewModel.onEvent(FocusEvent.StartTimer)
                        }
                    }) {
                        if (state.value.isRunning) {
                            Text("Durdur")
                        } else {
                            Text("Başlat")
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = {
                        viewModel.onEvent(FocusEvent.StopTimer)
                        viewModel.onEvent(FocusEvent.Tick)
                    }) {
                        Text("Sıfırla")
                    }
                }
            }
            if (state.value.selectedBook.id != -1L){
                Card(
                    modifier = Modifier
                        .height(230.dp)
                        .fillMaxWidth(),
                    onClick = {
                        navController.navigate(Page.NotesPage.route + "?bookId=${state.value.selectedBook.id}")
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.value.selectedBook.bookImagePath.isEmpty()) {
                            Image(
                                modifier = Modifier
                                    .height(200.dp)
                                    .width(150.dp),
                                painter = painterResource(id = R.drawable.blue_book),
                                contentDescription = "Books Grid",)
                        } else{
                            AsyncImage(
                                model = state.value.selectedBook.bookImagePath,
                                contentDescription = "Book Image",
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(150.dp)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentHeight()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = state.value.selectedBook.title,
                                fontSize = 36.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                            Text(
                                text = "Author: " +  state.value.selectedBook.author,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                            Text(
                                text = "Publisher: " + state.value.selectedBook.publisher,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                            Text(
                                text = "Language: " + state.value.selectedBook.language,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                            if (state.value.selectedBook.status == Book.BookStatus.READING) {
                                Text(
                                    text = "Currently Reading",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            } else {
                                Text(
                                    text = "To Read",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            ElevatedButton(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .height(50.dp)
                    .width(200.dp),
                onClick = {
                    isBottomSheetOpened = !isBottomSheetOpened
                }
            ) {
                Text("Kitap Seç")
                Icon(
                    imageVector = Icons.Filled.MenuBook,
                    contentDescription = "Choose Book"
                )
            }
        }
        if (isBottomSheetOpened) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight(0.5f),
                onDismissRequest = {
                    isBottomSheetOpened = false
                },
            ) {
                if (state.value.books.isEmpty()) {
                    Text(
                        text = "No books available",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                } else {
                    Text(
                        text = "Select a Book",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
                LazyColumn {
                    items(viewModel.state.value.books){  book ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            onClick = {
                                viewModel.onEvent(FocusEvent.SelectBook(book))
                                isBottomSheetOpened = false
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (book.bookImagePath.isEmpty()) {
                                    Image(
                                        modifier = Modifier
                                            .height(200.dp)
                                            .width(150.dp),
                                        painter = painterResource(id = R.drawable.blue_book),
                                        contentDescription = "Books Grid",
                                    )

                                } else{
                                    AsyncImage(
                                        model = book.bookImagePath,
                                        contentDescription = "Book Image",
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(150.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = book.title,
                                        fontSize = 36.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "Author: " +  book.author,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "Publisher: " + book.publisher,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "Language: " + book.language,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    if (book.status == Book.BookStatus.READING) {
                                        Text(
                                            text = "Currently Reading",
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "To Read",
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }

                            }
                        }
                        if (book != state.value.books.last()) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}