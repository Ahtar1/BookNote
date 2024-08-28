package com.example.booknote.presentation.books.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.booknote.domain.model.Book

@Composable
fun AddBookDialog(
    onDismiss: () -> Unit,
    onConfirm: (book: Book) -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier
                .fillMaxWidth(0.80f)
                .fillMaxHeight(0.5f)
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(15.dp)
                )
        ) {
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 15.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var title by remember { mutableStateOf("") }
                    var author by remember { mutableStateOf("") }
                    var publisher by remember { mutableStateOf("") }
                    var language by remember { mutableStateOf("") }
                    val focusManager = LocalFocusManager.current
                    val focusRequester = remember { FocusRequester() }

                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }

                    OutlinedTextField(
                        modifier = Modifier
                            .focusRequester(focusRequester),
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Author") },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    OutlinedTextField(
                        value = publisher,
                        onValueChange = { publisher = it },
                        label = { Text("Publisher") },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    OutlinedTextField(
                        value = language,
                        onValueChange = { language = it },
                        label = { Text("Language") },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    ElevatedButton(
                        modifier = Modifier
                            .padding(top = 15.dp),
                        onClick = {
                            onConfirm(
                                Book(
                                    title = title,
                                    author = author,
                                    publisher = publisher,
                                    language = language
                                )
                            )
                        }
                    ) {
                        Text(text = "Confirm")
                    }
                }
            }
            IconButton(
                onClick = { onDismiss() },
                modifier = Modifier.padding(top = 10.dp, end = 10.dp, bottom = 10.dp)
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                )
            }
        }
    }
}