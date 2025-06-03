package com.example.booknote.presentation.notes

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.TextFormat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booknote.domain.model.Note
import com.example.booknote.domain.util.NotesSortOrder
import com.example.booknote.presentation.notes.components.SortBottomSheet
import com.example.booknote.presentation.notes.components.ToggleItem
import com.example.booknote.presentation.util.Page
import com.example.booknote.presentation.util.record.ExoPlayer
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesPage(
    navController: NavController,
    bookId: Long,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val lazyColumnState = rememberLazyListState()

    var selectionMode by remember { mutableStateOf(false) }
    var searchMode by remember { mutableStateOf(false) }
    var selectedNotes by remember { mutableStateOf(listOf<Note>()) }
    var selectedBottomSheetItem by remember { mutableStateOf<ToggleItem?>(ToggleItem(5, "Date Created Descending", NotesSortOrder.DateCreatedDesc),) }
    val navBackStackEntry = remember { navController.currentBackStackEntry }
    val lifecycleOwner = LocalLifecycleOwner.current
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    val selectedTags = remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(navBackStackEntry) {
        viewModel.onEvent(NotesEvent.GetNotes(bookId = bookId.toString(), searchQuery = ""))
    }

    LaunchedEffect(state.order) {
        viewModel.onEvent(NotesEvent.GetNotes(bookId = bookId.toString(), searchQuery = state.searchQuery, notesSortOrder = state.order))
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(NotesEvent.GetTags)
        viewModel.onEvent(NotesEvent.GetBook(bookId))
        viewModel.onEvent(NotesEvent.GetNotes(bookId = bookId.toString(), searchQuery = ""))
        savedStateHandle?.getLiveData<Boolean>("refresh")?.observe(lifecycleOwner) { shouldRefresh ->
            if (shouldRefresh == true) {
                viewModel.onEvent(NotesEvent.GetNotes(bookId = bookId.toString(), searchQuery = ""))
                savedStateHandle.remove<Boolean>("refresh")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectionMode) "${selectedNotes.size} Seçildi" else state.book.title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                },
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
                    if (selectionMode) {
                        IconButton(onClick = {
                            viewModel.onEvent(NotesEvent.DeleteNotes(selectedNotes))
                            selectionMode = false
                            selectedNotes = emptyList()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Selected Notes"
                            )
                        }
                    } else{
                        IconButton(
                            onClick = {
                                searchMode = !searchMode
                            }
                        ){
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search"
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            modifier = Modifier.size(34.dp),
                            onClick = {
                                viewModel.onEvent(NotesEvent.OrderButtonClicked)
                            }) {
                            Icon(
                                modifier = Modifier.size(34.dp),
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "Sort"
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            navController.navigate(
                                Page.FocusPage.route + "?bookId=${bookId}"
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = "Check"
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            navController.navigate(
                                Page.BookDetailsPage.route + "?bookId=${bookId}"
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Book Info"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            val itemList = listOf(
                FABItem(
                    icon = Icons.Rounded.TextFormat,
                    text = "Note"
                ),
                FABItem(
                    icon = Icons.Rounded.Audiotrack,
                    text = "Audio"
                ),
                FABItem(
                    icon = Icons.Rounded.Draw,
                    text = "Draw"
                )
            )

            CustomExpandableFAB(
                items = itemList,
                onItemClick = { item ->
                    when (item.text) {
                        "Note" -> navController.navigate(
                            Page.AddNotePage.route + "?bookId=${bookId}"
                        )
                        "Audio" -> navController.navigate(
                            Page.AddAudioPage.route + "?bookId=${bookId}"
                        )
                        "Draw" -> navController.navigate(
                            Page.DrawNotePage.route + "?bookId=${bookId}"
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
        ) {
            var query by remember { mutableStateOf("") }
            var active by remember { mutableStateOf(false) }
            if (searchMode) {
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp, start = 8.dp, bottom = 8.dp, end = 8.dp),
                    query = query,
                    onQueryChange = {
                        query = it
                        scope.launch {
                            delay(500)
                            viewModel.onEvent(NotesEvent.GetNotes(bookId.toString(), query))
                        } },
                    onSearch = { newQuery ->
                        active = false
                        viewModel.onEvent(NotesEvent.GetNotes(bookId.toString(), newQuery)) },
                    active = false,
                    onActiveChange = { active = it },
                    placeholder = {
                        Text(text = "Search") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "searchIcon") },
                    content = {},)
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, start = 8.dp, bottom = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(viewModel.state.value.tags){
                    Card(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .wrapContentSize()
                            .height(30.dp)
                            .clickable{
                                if (selectedTags.value.contains(it)) {
                                    selectedTags.value -= it
                                } else {
                                    selectedTags.value += it
                                }
                                viewModel.onEvent(NotesEvent.UpdateNotesByTags(selectedTags.value))
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedTags.value.contains(it)) Color(0xff54b7de) else Color(0xFF4CAF50),
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier,
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if(selectedTags.value.contains(it)){
                                Icon(
                                    modifier = Modifier
                                    ,
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Check",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

            LazyColumn(
                state = lazyColumnState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(
                    items = state.notes,
                    key = {
                        it.id
                    }
                ) { note ->
                    val richTextState = rememberRichTextState()
                    LaunchedEffect(state.notes) {
                        richTextState.setHtml(note.noteText ?: "")
                    }

                    val isSelected = selectedNotes.contains(note)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        selectionMode = true
                                        if (selectedNotes.contains(note)) {
                                            selectedNotes = selectedNotes - note
                                            if (selectedNotes.isEmpty()) {
                                                selectionMode = false
                                            }
                                        } else {
                                            selectedNotes = selectedNotes + note
                                        }
                                    },
                                    onTap = {
                                        if (selectionMode) {
                                            if (selectedNotes.contains(note)) {
                                                selectedNotes = selectedNotes - note
                                                if (selectedNotes.isEmpty()) {
                                                    selectionMode = false
                                                }
                                            } else {
                                                selectedNotes = selectedNotes + note
                                            }
                                        } else {
                                            if (note.isDrawn) {
                                                navController.navigate(
                                                    Page.DrawNotePage.route + "?bookId=${bookId}&noteId=${note.id}"
                                                )
                                            } else if (note.audioFilePath == null) {
                                                navController.navigate(
                                                    Page.AddNotePage.route + "?bookId=${bookId}&noteId=${note.id}"
                                                )
                                            }
                                        }
                                    }
                                )
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color.LightGray else Color(note.color),
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = note.noteTitle,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 10,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Page: ${note.page}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 10,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                items(note.tags){
                                    Card(
                                        modifier = Modifier
                                            .padding(end = 4.dp)
                                            .height(30.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xff54b7de),
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text(
                                            modifier = Modifier
                                                .padding(vertical = 4.dp, horizontal = 8.dp),
                                            text = it,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            if (note.noteText != null) {
                                Box(
                                    modifier = Modifier.pointerInput(Unit) {
                                        detectTapGestures(
                                            onLongPress = {
                                                selectionMode = true
                                                if (selectedNotes.contains(note)) {
                                                    selectedNotes = selectedNotes - note
                                                    if (selectedNotes.isEmpty()) {
                                                        selectionMode = false
                                                    }
                                                } else {
                                                    selectedNotes = selectedNotes + note
                                                }
                                            },
                                            onTap = {
                                                if (selectionMode) {
                                                    if (selectedNotes.contains(note)) {
                                                        selectedNotes = selectedNotes - note
                                                        if (selectedNotes.isEmpty()) {
                                                            selectionMode = false
                                                        }
                                                    } else {
                                                        selectedNotes = selectedNotes + note
                                                    }
                                                } else {
                                                    if (note.isDrawn) {
                                                        navController.navigate(
                                                            Page.DrawNotePage.route + "?bookId=${bookId}&noteId=${note.id}"
                                                        )
                                                    } else if (note.audioFilePath == null) {
                                                        navController.navigate(
                                                            Page.AddNotePage.route + "?bookId=${bookId}&noteId=${note.id}"
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                    },
                                ) {
                                    RichText(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        state = richTextState,
                                        lineHeight = 36.sp,
                                    )
                                }

                            }
                            if (note.audioFilePath != null) {
                                ExoPlayer(Uri.fromFile(File(note.audioFilePath)))
                            }
                            note.imageFilePath?.let { imagePath ->
                                val imageFile = File(imagePath)
                                if (imageFile.exists()) {
                                    val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(300.dp)
                                                .padding(10.dp)
                                        )
                                    }
                                }
                            }
                            IconButton(
                                modifier = Modifier
                                    .size(34.dp)
                                    .align(Alignment.Start)
                                    .padding(top = 8.dp),
                                onClick = {
                                    viewModel.onEvent(NotesEvent.ChangeFavorite(note))
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Favorite",
                                    tint = if (note.favorite) Color(0xff54b7de) else Color.Gray,
                                    modifier = Modifier
                                        .size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (viewModel.isBottomSheetShown) {
            SortBottomSheet(
                onDismissRequest = { viewModel.onEvent(NotesEvent.DismissBottomSheet) },
                sortItems = listOf(
                    ToggleItem(0, "Note Title Ascending", NotesSortOrder.NoteTitleAsc),
                    ToggleItem(1, "Note Title Descending", NotesSortOrder.NoteTitleDesc),
                    ToggleItem(2, "Page Ascending", NotesSortOrder.PageAsc),
                    ToggleItem(3, "Page Descending", NotesSortOrder.PageDesc),
                    ToggleItem(4, "Date Created Ascending", NotesSortOrder.DateCreatedAsc),
                    ToggleItem(5, "Date Created Descending", NotesSortOrder.DateCreatedDesc),
                ),
                initialSelectedItem = selectedBottomSheetItem,
                onItemSelected = { newItem -> selectedBottomSheetItem = newItem },
                onClick = { order ->
                    viewModel.onEvent(NotesEvent.ChangeOrder(order)); viewModel.onEvent(NotesEvent.DismissBottomSheet)

                }
            )
        }
    }
}

data class FABItem(
    val icon: ImageVector,
    val text: String
)

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun CustomExpandableFAB(
    modifier: Modifier = Modifier,
    items: List<FABItem>,
    fabButton: FABItem = FABItem(icon = Icons.Rounded.Add, text = "Add"),
    onItemClick: (FABItem) -> Unit
) {

    var buttonClicked by remember {
        mutableStateOf(false)
    }

    val interactionSource = MutableInteractionSource()

    Card(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Column {
            AnimatedVisibility(
                visible = buttonClicked,
                enter = expandVertically(tween(1500)) + fadeIn(),
                exit = shrinkVertically(tween(1200)) + fadeOut(
                    animationSpec = tween(1000)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 20.dp, horizontal = 30.dp)
                ) {
                    items.forEach { item ->
                        Row(modifier = Modifier
                            .padding(vertical = 10.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = {
                                    onItemClick(item)
                                    buttonClicked = false
                                }
                            )) {
                            Icon(
                                imageVector = item.icon, contentDescription = "refresh"
                            )

                            Spacer(modifier = Modifier.width(15.dp))

                            Text(text = item.text)
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        buttonClicked = !buttonClicked
                    }
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 20.dp, horizontal = 30.dp)
                ) {
                    Icon(
                        imageVector = fabButton.icon, contentDescription = "refresh"
                    )
                    AnimatedVisibility(
                        visible = buttonClicked,
                        enter = expandVertically(animationSpec = tween(1500)) + fadeIn(),
                        exit = shrinkVertically(tween(1200)) + fadeOut(tween(1200))
                    ) {
                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(text = fabButton.text)
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                    }
                }
            }

        }

    }
}

fun saveImageToInternalStorage(context: Context, uri: Uri): File? {
    return try {

        val inputStream = context.contentResolver.openInputStream(uri)
        val imageFile = File(context.getExternalFilesDir(null), "image_${System.currentTimeMillis()}.jpg")
        val outputStream = imageFile.outputStream()

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        imageFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}