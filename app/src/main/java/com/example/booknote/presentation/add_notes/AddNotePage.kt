import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booknote.domain.model.Note
import com.example.booknote.presentation.add_notes.AddNotesEvent
import com.example.booknote.presentation.add_notes.AddNotesViewModel
import com.example.booknote.presentation.notes.saveImageToInternalStorage
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

    val context = LocalContext.current

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    var title by remember { mutableStateOf("") }
    var pageNumber by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val imageFile = saveImageToInternalStorage(context, uri)
            imagePath = imageFile?.absolutePath ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Add Note")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        launcher.launch("image/*")
                    }) {
                        Icon(imageVector = Icons.Filled.Image, contentDescription = "Add Image Button")
                    }
                    IconButton(onClick = {
                        viewModel.onEvent(
                            AddNotesEvent.AddNote(
                                Note(
                                    id = noteId ?: 0,
                                    noteTitle = title,
                                    noteText = textFieldValue.text,
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
                textFieldValue = textFieldValue.copy(text = state.note.noteText.toString())
                title = state.note.noteTitle
                pageNumber = state.note.page.toString()
                imagePath = state.note.imageFilePath.toString()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            var focusRequester: FocusRequester? = null

            if(noteId == null){
                focusRequester = remember { FocusRequester() }

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
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

            if (imagePath != ""){
                val bitmap = BitmapFactory.decodeFile(imagePath)
                ZoomableImageWithBlurDynamic(bitmap = bitmap)
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
                        modifier = if(noteId == null) Modifier
                            .bringIntoViewRequester(bringIntoViewRequester)
                            .fillMaxSize()
                            .focusRequester(focusRequester!!)
                            else Modifier
                            .bringIntoViewRequester(bringIntoViewRequester)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun ZoomableImageWithBlurDynamic(
    bitmap: Bitmap,
) {
    var isZoomed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .border(2.dp, Color.Gray, RoundedCornerShape(2.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .clickable {
                    isZoomed = true
                },
            contentScale = ContentScale.Crop
        )

        if (isZoomed) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable {
                        isZoomed = false
                    }
            )

            Dialog(onDismissRequest = { isZoomed = false }) {
                Box(
                    modifier = Modifier
                        .background(Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        IconButton(onClick = { isZoomed = false }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close"
                            )

                        }
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier,
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}
