package com.example.booknote.presentation.draw_note

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LineWeight
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.getstream.sketchbook.PaintColorPalette
import io.getstream.sketchbook.PaintColorPaletteTheme
import io.getstream.sketchbook.Sketchbook
import io.getstream.sketchbook.rememberSketchbookController
import java.io.File
import java.io.FileOutputStream

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawNotePage(
    navController: NavController,
    bookId: Long,
    viewModel: DrawNoteViewModel = hiltViewModel()
) {

    var saved = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val sketchbookController = rememberSketchbookController()
    var expanded by remember { mutableStateOf(false) }
    var selectedLineWeight by remember { mutableFloatStateOf(8f) }

    sketchbookController.setPaintColor(Color.Black)
    sketchbookController.setPaintStrokeWidth(selectedLineWeight)

    fun saveBitmapToExternal(){

    }

    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, fileName: String): String? {
        return try {
            val file = File(context.filesDir, "$fileName.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Draw",
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
                    IconButton(onClick = {
                        saveBitmapToInternalStorage(context = context, sketchbookController.getSketchbookBitmap().asAndroidBitmap(),"")
                        viewModel.onEvent(DrawNoteEvent.SaveNote(
                            bookId = bookId,
                            title = "Title",
                            content = "Content",
                            image = sketchbookController.getSketchbookBitmap().asAndroidBitmap()
                        ))
                        println(sketchbookController.getSketchbookBitmap())
                        saved.value = true
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

        if (saved.value){
            Image(bitmap = sketchbookController.getSketchbookBitmap(), contentDescription = "")
        } else {

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
                        backgroundColor = Color.Black,
                    )
                    PaintColorPalette(
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .fillMaxWidth()
                            .border(1.dp, Color.Black),
                        theme = PaintColorPaletteTheme(
                            shape = CircleShape,
                            itemSize = 48.dp,
                            selectedItemSize = 58.dp,
                            borderColor = Color.Black,
                            borderWidth = 2.dp,
                        ),
                        controller = sketchbookController,
                        initialSelectedIndex = 0,
                        colorList = listOf(
                            Color.Black,
                            Color.Yellow,
                            Color.Red,
                            Color.Green,
                            Color.Blue,
                            Color.Cyan,
                        ),
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
                                        selectedLineWeight = 4f
                                    },
                                    text = { Text("İnce") },
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
                                        selectedLineWeight = 8f
                                    },
                                    text = { Text("Orta") },
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
                                        selectedLineWeight = 12f
                                    },
                                    text = { Text("Kalın") },
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
                                        selectedLineWeight = 16f
                                    },
                                    text = { Text("Çok Kalın") },
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
        }
    }
}