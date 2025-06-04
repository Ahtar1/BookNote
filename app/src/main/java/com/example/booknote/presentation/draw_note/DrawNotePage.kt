package com.example.booknote.presentation.draw_note

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LineWeight
import androidx.compose.material.icons.filled.More
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booknote.R
import com.example.booknote.presentation.add_audio.components.SaveBottomSheet
import com.example.booknote.presentation.add_notes.components.TagsBottomSheet
import com.example.booknote.presentation.notes.saveImageToInternalStorage
import io.getstream.sketchbook.PaintColorPalette
import io.getstream.sketchbook.PaintColorPaletteTheme
import io.getstream.sketchbook.Sketchbook
import io.getstream.sketchbook.rememberSketchbookController

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawNotePage(
    navController: NavController,
    bookId: Long,
    noteId: Long?,
    viewModel: DrawNoteViewModel = hiltViewModel()
) {

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val sketchbookController = rememberSketchbookController()
    var expanded by remember { mutableStateOf(false) }
    var selectedLineWeight by remember { mutableFloatStateOf(8f) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imagePath by remember { mutableStateOf("") }
    val colorList by remember { mutableStateOf(listOf(Color.Black, Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta, Color.Cyan, Color.White)) }

    var tagsBottomSheetExpanded by remember { mutableStateOf(false) }

    val state = viewModel.state.value

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val imageFile = saveImageToInternalStorage(context, uri)
            imagePath = imageFile?.absolutePath ?: ""
            imageBitmap = BitmapFactory.decodeFile(imageFile?.absolutePath)
        }
    }

    sketchbookController.setPaintColor(Color.Black)
    sketchbookController.setPaintStrokeWidth(selectedLineWeight)
    imageBitmap?.let {
        sketchbookController.setImageBitmap(it.asImageBitmap())
    }

    LaunchedEffect(noteId) {
        noteId?.let {
            viewModel.onEvent(DrawNoteEvent.GetNote(it))
        }
    }

    LaunchedEffect(state.note.imageFilePath) {
        state.note.imageFilePath?.let {
            val bitmap = BitmapFactory.decodeFile(it)?.asImageBitmap()
            sketchbookController.setImageBitmap(bitmap)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.draw),
                        style = TextStyle(fontSize = 20.sp)
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
                actions = {
                    IconButton(
                        onClick = {
                            tagsBottomSheetExpanded = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.More,
                            contentDescription = "Add Tag"
                        )
                    }
                    IconButton(onClick = {
                        launcher.launch("image/*")
                    }) {
                        Icon(imageVector = Icons.Filled.Image, contentDescription = "Add Image Button")
                    }

                    IconButton(onClick = {
                        capturedBitmap = sketchbookController.getSketchbookBitmap().asAndroidBitmap()
                        viewModel.onEvent(DrawNoteEvent.SaveButtonClicked)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = ""
                        )
                    }
                }
            )
        },
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column {
                Sketchbook(
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .fillMaxWidth(),
                    controller = sketchbookController,
                    )
                Spacer(modifier = Modifier.height(2.dp))
                PaintColorPalette(
                    modifier = Modifier
                        .border(1.dp, Color.Black),
                    theme = PaintColorPaletteTheme(
                        shape = CircleShape,
                        itemSize = 48.dp,
                        selectedItemSize = 58.dp,
                        borderColor = Color.Black,
                        borderWidth = 2.dp,),
                    controller = sketchbookController,
                    initialSelectedIndex = 0,
                    colorList = colorList,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { sketchbookController.undo() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = "ArrowBackIosNew"
                        )
                    }
                    Column {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Filled.LineWeight,
                                contentDescription = "Line Weight"
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    sketchbookController.setPaintStrokeWidth(4f)
                                    expanded = false
                                    selectedLineWeight = 4f },
                                text = { Text(stringResource(R.string.thin)) },
                                enabled = selectedLineWeight != 4f,
                                trailingIcon = {
                                    if (selectedLineWeight == 4f)
                                        Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = "Done"
                                        )
                                }
                            )
                            DropdownMenuItem(
                                onClick = {
                                    sketchbookController.setPaintStrokeWidth(8f)
                                    expanded = false
                                    selectedLineWeight = 8f },
                                text = { Text(stringResource(R.string.medium)) },
                                enabled = selectedLineWeight != 8f,
                                trailingIcon = {
                                    if (selectedLineWeight == 8f)
                                        Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = "Done"
                                        )
                                }
                            )
                            DropdownMenuItem(
                                onClick = {
                                    sketchbookController.setPaintStrokeWidth(12f)
                                    expanded = false
                                    selectedLineWeight = 12f },
                                text = { Text(stringResource(R.string.thick)) },
                                enabled = selectedLineWeight != 12f,
                                trailingIcon = {
                                    if (selectedLineWeight == 12f)
                                        Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = "Done"
                                        )
                                }
                            )
                            DropdownMenuItem(
                                onClick = {
                                    sketchbookController.setPaintStrokeWidth(16f)
                                    expanded = false
                                    selectedLineWeight = 16f },
                                text = { Text(stringResource(R.string.very_thick)) },
                                enabled = selectedLineWeight != 16f,
                                trailingIcon = {
                                    if (selectedLineWeight == 16f)
                                        Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = "Done"
                                        )
                                }
                            )
                        }
                    }
                }
            }
        }

        if (tagsBottomSheetExpanded){
            TagsBottomSheet(
                onDismissRequest = { tagsBottomSheetExpanded = false},
                onSave = { tags ->
                    viewModel.onEvent(DrawNoteEvent.SaveTags(tags, noteId))
                    tagsBottomSheetExpanded = false
                },
                tags = viewModel.state.value.note.tags,
            )
        }

        if (viewModel.isBottomSheetShown){

            SaveBottomSheet(
                onDismissRequest = { viewModel.onEvent(DrawNoteEvent.DismissBottomSheet) },
                onSave = { title, page, newColor ->
                    capturedBitmap?.let { capturedBitmap ->
                        viewModel.onEvent(DrawNoteEvent.SaveNote(
                            noteId = noteId ?: 0,
                            bookId = bookId,
                            title = title,
                            image = capturedBitmap,
                            context = context,
                            page = page.toInt(),
                            color = newColor
                        ))
                    }

                    navController.navigateUp()
                },
                oldTitle = state.note.noteTitle,
                oldPage = state.note.page,
                oldColor = state.note.color,
            )
        }
    }
}