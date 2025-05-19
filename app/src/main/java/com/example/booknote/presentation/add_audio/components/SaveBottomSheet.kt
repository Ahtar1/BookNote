package com.example.booknote.presentation.add_audio.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveBottomSheet(
    onDismissRequest: () -> Unit,
    onSave: (String, Long, Long) -> Unit,
    oldTitle: String? = null,
    oldPage: Int? = null,
    oldColor: Long? = null,
) {
    val colorList = remember { listOf(
        0xFFFFCDD2, // Light Red
        0xFFF8BBD0, // Light Pink
        0xFFE1BEE7, // Light Purple
        0xFFD1C4E9, // Light Deep Purple
        0xFFC5CAE9, // Light Indigo
        0xFFBBDEFB, // Light Blue
        0xFFB3E5FC, // Light Cyan
        0xFFB2EBF2, // Light Teal
        0xFFC8E6C9, // Light Green
        0xFFDCE775, // Lime
        0xFFFFF59D, // Yellow
        0xFFFFE082, // Amber
        0xFFFFCCBC, // Deep Orange
        0xFFD7CCC8, // Brown
        0xFFCFD8DC  // Blue Grey
    ) }

    val selectedColor = remember { mutableStateOf(colorList[0]) }

    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var title by remember {
                mutableStateOf(oldTitle ?:"")
            }
            var pageNumber by remember {
                mutableStateOf((oldPage ?: "").toString())
            }

            Text(
                text = "Select a color for the background",
                style = MaterialTheme.typography.bodyMedium
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(oldColor?.let { Color(it) } ?: Color(colorList[0]))
                    .border(1.dp, Color.LightGray )
            ) {
                items(colorList) { color ->
                    if(selectedColor.value == color) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color(color))
                                .border(2.dp, Color.DarkGray)
                                .clickable {
                                    selectedColor.value = color
                                }
                        )
                    } else
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color(color))
                            .clickable {
                                selectedColor.value = color
                            }
                    )
                }
            }

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                value = title,
                onValueChange = {title = it},
                label = { Text("Title") },
                singleLine = true,
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                maxLines = 1,
                label = { Text("Page Number") },
                value = if (pageNumber == "0" ) "" else pageNumber,
                onValueChange = { newText ->
                    if (newText.all { it.isDigit() }) {
                        pageNumber = newText
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 25.sp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
            )

            ElevatedButton(
                modifier = Modifier.padding(bottom = 24.dp),
                onClick = {
                onSave(title, pageNumber.toLong(), selectedColor.value)
            }
            ) {
                Text(text ="Save")
            }
        }
    }
}