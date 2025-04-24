import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.More
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booknote.domain.model.Note
import com.example.booknote.presentation.add_notes.AddNotesEvent
import com.example.booknote.presentation.add_notes.AddNotesViewModel
import com.example.booknote.presentation.add_notes.components.TagsBottomSheet
import com.example.booknote.presentation.notes.saveImageToInternalStorage
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNotePage(
    navController: NavController,
    bookId: Long,
    noteId: Long?,
    noteColor: Long?,
    viewModel: AddNotesViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    val richTextState = rememberRichTextState()

    var title by remember { mutableStateOf("") }
    var pageNumber by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf("") }

    var tag by remember { mutableStateOf("") }

    var topAppBarColor by remember { mutableStateOf(noteColor ?: 0xffD8EFD3) }

    val titleSize = MaterialTheme.typography.displaySmall.fontSize
    val subtitleSize = MaterialTheme.typography.titleLarge.fontSize

    var dropdownMenuExpanded by remember { mutableStateOf(false) }
    var tagsBottomSheetExpanded by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val imageFile = saveImageToInternalStorage(context, uri)
            imagePath = imageFile?.absolutePath ?: ""
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = Color(topAppBarColor)
                ),
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
                        viewModel.onEvent(AddNotesEvent.ToggleColorPicker)
                    }) {
                        Icon(imageVector = Icons.Filled.ColorLens, contentDescription = "Note Color")
                    }
                    IconButton(onClick = {
                        launcher.launch("image/*")
                    }) {
                        Icon(imageVector = Icons.Filled.Image, contentDescription = "Add Image Button")
                    }
                    IconButton(onClick = {
                        if (title.isEmpty() && pageNumber.isEmpty()){
                            Toast.makeText(context, "Please fill in the title and page number", Toast.LENGTH_SHORT).show()
                        } else{
                            viewModel.onEvent(
                                AddNotesEvent.AddNote(
                                    Note(
                                        id = noteId ?: 0,
                                        noteTitle = title,
                                        noteText = richTextState.toHtml(),
                                        imageFilePath = imagePath,
                                        page = pageNumber.toIntOrNull() ?: 0,
                                        dateCreated = LocalDateTime.now().format(
                                            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                                        ),
                                        color = viewModel.state.value.note.color,
                                        bookId = bookId,
                                        tags = viewModel.state.value.note.tags
                                    )
                                )
                            )
                            navController.navigateUp()
                        }
                    }) {
                        Icon(imageVector = Icons.Filled.Save, contentDescription = "Save Note")
                    }

                    Box(
                        modifier = Modifier
                    ) {
                        IconButton(onClick = { dropdownMenuExpanded = !dropdownMenuExpanded }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = dropdownMenuExpanded,
                            onDismissRequest = { dropdownMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Add Tag") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.More,
                                        contentDescription = "Add Tag"
                                    )
                                },
                                onClick = {
                                    tagsBottomSheetExpanded = true
                                    dropdownMenuExpanded = false
                                }
                            )
                        }
                    }
                })
        },
    ) { paddingValues ->

        val state = viewModel.state.value

        LaunchedEffect(state.note.color) {
            topAppBarColor = state.note.color
        }

        LaunchedEffect(noteId) {
            noteId?.let {
                viewModel.onEvent(AddNotesEvent.GetNote(it))
            }
        }

        LaunchedEffect(state.note) {
            if (noteId != null) {
                richTextState.setHtml(state.note.noteText.toString())
                title = state.note.noteTitle
                pageNumber = state.note.page.toString()
                imagePath = state.note.imageFilePath.toString()
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Color(viewModel.state.value.note.color)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (viewModel.isColorPickerShown) {
                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color(viewModel.state.value.note.color))
                            .border(1.dp, Color.LightGray )
                    ) {
                        items(viewModel.colorList) { color ->
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(Color(color))
                                    .clickable {
                                        viewModel.onEvent(AddNotesEvent.ChangeColor(color))
                                        viewModel.onEvent(AddNotesEvent.ToggleColorPicker)
                                    }
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp)
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
            }

            item {
                if (imagePath != ""){
                    val bitmap = BitmapFactory.decodeFile(imagePath)
                    ZoomableImageWithBlurDynamic(bitmap = bitmap)
                }
            }

            item{
                EditorControls(
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    state = richTextState,
                    onBoldClick = {
                        richTextState.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    },
                    onItalicClick = {
                        richTextState.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    },
                    onUnderlineClick = {
                        richTextState.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                    },
                    onTitleClick = {
                        richTextState.toggleSpanStyle(SpanStyle(fontSize = titleSize))
                    },
                    onSubtitleClick = {
                        richTextState.toggleSpanStyle(SpanStyle(fontSize = subtitleSize))
                    },
                    onTextColorClick = {
                        richTextState.toggleSpanStyle(SpanStyle(color = Color.Red))
                    },
                )
                RichTextEditor(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = richTextState,
                    colors = RichTextEditorDefaults.richTextEditorColors(
                        containerColor = Color(viewModel.state.value.note.color),
                    ),
                )
            }
        }
        if (tagsBottomSheetExpanded){
            TagsBottomSheet(
                onDismissRequest = { tagsBottomSheetExpanded = false},
                onSave = { tags ->
                    viewModel.onEvent(AddNotesEvent.SaveTags(tags, noteId))
                    tagsBottomSheetExpanded = false
                },
                tags = viewModel.state.value.note.tags,
            )
        }
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditorControls(
    modifier: Modifier = Modifier,
    state: RichTextState,
    onBoldClick: () -> Unit,
    onItalicClick: () -> Unit,
    onUnderlineClick: () -> Unit,
    onTitleClick: () -> Unit,
    onSubtitleClick: () -> Unit,
    onTextColorClick: () -> Unit,
) {
    var boldSelected by rememberSaveable { mutableStateOf(false) }
    var italicSelected by rememberSaveable { mutableStateOf(false) }
    var underlineSelected by rememberSaveable { mutableStateOf(false) }
    var titleSelected by rememberSaveable { mutableStateOf(false) }
    var subtitleSelected by rememberSaveable { mutableStateOf(false) }
    var textColorSelected by rememberSaveable { mutableStateOf(false) }


    val titleSize = MaterialTheme.typography.displaySmall.fontSize
    val subtitleSize = MaterialTheme.typography.titleLarge.fontSize

    LaunchedEffect(state.currentSpanStyle) {
        boldSelected = state.currentSpanStyle.fontWeight == FontWeight.Bold
        italicSelected = state.currentSpanStyle.fontStyle == FontStyle.Italic
        underlineSelected = state.currentSpanStyle.textDecoration == TextDecoration.Underline
        titleSelected = state.currentSpanStyle.fontSize == titleSize
        subtitleSelected = state.currentSpanStyle.fontSize == subtitleSize
        textColorSelected = state.currentSpanStyle.color == Color.Red
    }

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 10.dp)
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ControlWrapper(
            selected = boldSelected,
            onChangeClick = { boldSelected = it },
            onClick = onBoldClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatBold,
                contentDescription = "Bold Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = italicSelected,
            onChangeClick = { italicSelected = it },
            onClick = onItalicClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatItalic,
                contentDescription = "Italic Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = underlineSelected,
            onChangeClick = { underlineSelected = it },
            onClick = onUnderlineClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatUnderlined,
                contentDescription = "Underline Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = titleSelected,
            onChangeClick = {
                titleSelected = it
                if (subtitleSelected) subtitleSelected = false
                            },
            onClick = onTitleClick
        ) {
            Icon(
                imageVector = Icons.Default.Title,
                contentDescription = "Title Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = subtitleSelected,
            onChangeClick = {
                subtitleSelected = it
                if (titleSelected) titleSelected = false
                            },
            onClick = onSubtitleClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatSize,
                contentDescription = "Subtitle Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = textColorSelected,
            onChangeClick = { textColorSelected = it },
            onClick = onTextColorClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatColorText,
                contentDescription = "Text Color Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun ControlWrapper(
    selected: Boolean,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.inversePrimary,
    onChangeClick: (Boolean) -> Unit,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(size = 6.dp))
            .clickable {
                onClick()
                onChangeClick(!selected)
            }
            .background(
                if (selected) selectedColor
                else unselectedColor
            )
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(size = 6.dp)
            )
            .padding(all = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
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
            .padding(8.dp),
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
