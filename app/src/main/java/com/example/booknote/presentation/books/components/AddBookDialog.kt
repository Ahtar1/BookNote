package com.example.booknote.presentation.books.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.booknote.domain.model.Book

@Composable
fun AddBookDialog(
    onDismiss: () -> Unit,
    onConfirm: (book: Book) -> Unit
){
    Dialog(
        onDismissRequest = {onDismiss()},
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            ),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth(0.80f)
                .border(
                    width = 1.dp,
                    color = Color.Green,
                    shape = RoundedCornerShape(15.dp)
                )
        ) {
            Column(

            ) {
                var title by remember { mutableStateOf("") }
                var author by remember { mutableStateOf("") }
                var publisher by remember { mutableStateOf("") }
                var language by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author") }
                )
                OutlinedTextField(
                    value = publisher,
                    onValueChange = { publisher = it },
                    label = { Text("Publisher") }
                )
                OutlinedTextField(
                    value = language,
                    onValueChange = { language = it },
                    label = { Text("Language") }
                )
                ElevatedButton(
                    onClick = {
                        onConfirm(Book(
                            title = title,
                            author = author,
                            publisher = publisher,
                            language = language
                        ))
                    }
                ) {
                    Text(text = "Confirm")
                }
            }
        }
    }
}