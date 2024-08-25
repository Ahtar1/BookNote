import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booknote.domain.model.Note
import com.example.booknote.presentation.add_notes.AddNotesEvent
import com.example.booknote.presentation.add_notes.AddNotesViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddNotePage(
    navController: NavController,
    bookId: Long,
    noteId: Long?,
    viewModel: AddNotesViewModel = hiltViewModel()
) {

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    var content by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var pageNumber by remember { mutableStateOf("") }

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
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.onEvent(
                            AddNotesEvent.AddNote(
                                Note(
                                    id = noteId ?: 0,
                                    noteTitle = title,
                                    noteText = content,
                                    page = pageNumber.toIntOrNull() ?: 0,
                                    dateCreated = LocalDateTime.now().format(
                                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                                    ),
                                    bookId = bookId
                                )
                            )
                        )
                        navController.navigateUp()
                    }) {
                        Icon(imageVector = Icons.Filled.Save, contentDescription = "Save Note")
                    }
                })
        },
    ) { paddingValues ->

        val state = viewModel.state.value

        LaunchedEffect(noteId) {
            noteId?.let {
                viewModel.onEvent(AddNotesEvent.GetNote(it))
            }
        }

        LaunchedEffect(state.note) {
            if (noteId != null) {
                content = state.note.noteText.orEmpty()
                title = state.note.noteTitle.orEmpty()
                pageNumber = state.note.page.toString()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val focusRequester = remember { FocusRequester() }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(7f),
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    maxLines = 1,
                )

                OutlinedTextField(
                    modifier = Modifier
                        .weight(3f)
                        .padding(horizontal = 8.dp),
                    maxLines = 1,
                    label = { Text("Page") },
                    value = pageNumber,
                    onValueChange = { newText ->
                        if (newText.all { it.isDigit() }) {
                            pageNumber = newText
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number
                    ),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .verticalScroll(scrollState)
            ) {
                val customTextSelectionColors = TextSelectionColors(
                    handleColor = Transparent,
                    backgroundColor = Transparent,
                )

                CompositionLocalProvider(
                    LocalTextSelectionColors provides customTextSelectionColors,
                ) {
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = { newValue ->
                            textFieldValue = newValue
                        },
                        minLines = 12,
                        maxLines = 50,
                        textStyle = TextStyle(fontSize = 30.sp),
                        onTextLayout = {
                            val cursorRect = it.getCursorRect(textFieldValue.selection.start)
                            coroutineScope.launch {
                                bringIntoViewRequester.bringIntoView(cursorRect)
                            }
                        },
                        modifier = Modifier
                            .bringIntoViewRequester(bringIntoViewRequester)
                            .fillMaxSize()
                            .focusRequester(focusRequester)
                    )
                }
            }
        }
    }
}
