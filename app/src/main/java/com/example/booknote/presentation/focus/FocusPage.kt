package com.example.booknote.presentation.focus

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    bookId: Long,
    viewModel: FocusViewModel = hiltViewModel()
){
    var isBottomSheetOpened by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onEvent(FocusEvent.GetBooks)
        if (bookId != -1L) {
            viewModel.onEvent(FocusEvent.SetDefaultSelectedBook(bookId))
        }
    }
    val state = viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.focus)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = Color.Black,
                ),
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
        },
        contentColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedTimer(
                    elapsedSeconds = state.value.elapsedSeconds,
                    isRunning = state.value.isRunning,
                    onTick = {}
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            if (state.value.isRunning) {
                                viewModel.onEvent(FocusEvent.StopTimer)
                            } else {
                                viewModel.onEvent(FocusEvent.StartTimer)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        val label = when {
                            state.value.isRunning -> R.string.stop
                            state.value.elapsedSeconds > 0 -> R.string.resume
                            else -> R.string.start
                        }
                        Text(stringResource(label))
                    }

                    Button(onClick = {
                        viewModel.onEvent(FocusEvent.StopTimer)
                        viewModel.onEvent(FocusEvent.ResetTimer)
                    }) {
                        Text(stringResource(R.string.reset))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val selectedBook = state.value.selectedBook
            val bookImageBitmap = remember(selectedBook.bookImagePath) {
                getCorrectlyOrientedBitmap(selectedBook.bookImagePath)?.asImageBitmap()
            }

            if (selectedBook.id != -1L) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    onClick = {
                        navController.navigate(Page.NotesPage.route + "?bookId=${selectedBook.id}")
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if(bookId == -1L){
                            IconButton(
                                onClick = { viewModel.onEvent(FocusEvent.DeleteSelectedBook) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete Selected Book"
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (selectedBook.bookImagePath.isEmpty()) {
                                Image(
                                    painter = painterResource(id = R.drawable.blue_book),
                                    contentDescription = "Default Book Image",
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(120.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                bookImageBitmap?.let {
                                    Image(
                                        bitmap = it,
                                        contentDescription = "Book Image",
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(120.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text(
                                    text = selectedBook.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(text = "${stringResource(R.string.author)}: ${selectedBook.author}")
                                Text(text = "${stringResource(R.string.publisher)}: ${selectedBook.publisher}")
                                Text(text = "${stringResource(R.string.language)}: ${selectedBook.language}")
                                Text(
                                    text = if (selectedBook.status == Book.BookStatus.READING)
                                        stringResource(R.string.currently_reading)
                                    else
                                        stringResource(R.string.to_read)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (bookId == -1L) {
                ElevatedButton(
                    onClick = { isBottomSheetOpened = !isBottomSheetOpened },
                    modifier = Modifier
                        .height(50.dp)
                        .width(200.dp),
                    colors = ButtonDefaults.elevatedButtonColors()
                ) {
                    Icon(
                        imageVector = Icons.Filled.MenuBook,
                        contentDescription = "Choose Book",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(stringResource(R.string.select_book))
                }
            }
        }

        if (isBottomSheetOpened) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight(0.5f),
                onDismissRequest = {
                    isBottomSheetOpened = false
                },
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                if (state.value.books.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_books),
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.select_book),
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
                                .padding(12.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            onClick = {
                                viewModel.onEvent(FocusEvent.SelectBook(book))
                                isBottomSheetOpened = false
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                val imageModifier = Modifier
                                    .height(160.dp)
                                    .width(120.dp)
                                    .clip(RoundedCornerShape(12.dp))

                                if (book.bookImagePath.isEmpty()) {
                                    Image(
                                        painter = painterResource(id = R.drawable.blue_book),
                                        contentDescription ="Book Cover",
                                        modifier = imageModifier
                                    )
                                } else {
                                    val bitmap = remember(book.bookImagePath) {
                                        getCorrectlyOrientedBitmap(book.bookImagePath)?.asImageBitmap()
                                    }

                                    bitmap?.let {
                                        Image(
                                            bitmap = it,
                                            contentDescription = "Book Cover",
                                            modifier = imageModifier,
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }

                                // Metin Bilgileri
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = book.title,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "${stringResource(R.string.author)}: ${book.author}",
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${stringResource(R.string.publisher)}: ${book.publisher}",
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${stringResource(R.string.language)}: ${book.language}",
                                        fontSize = 14.sp
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = if (book.status == Book.BookStatus.READING)
                                            stringResource(R.string.currently_reading)
                                        else
                                            stringResource(R.string.to_read),
                                        fontSize = 14.sp,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        if (book != state.value.books.last()) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

fun getCorrectlyOrientedBitmap(filePath: String): Bitmap? {
    val originalBitmap = BitmapFactory.decodeFile(filePath) ?: return null

    val exif = ExifInterface(filePath)
    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

    val matrix = Matrix()

    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
        else -> return originalBitmap
    }

    return Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
}