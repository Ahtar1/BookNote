package com.example.booknote.presentation.add_audio.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveBottomSheet(
    onDismissRequest: () -> Unit,
    onSave: (String, Long) -> Unit,
    oldTitle: String? = null,
    oldPage: Int? = null
) {
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
                mutableStateOf(oldPage?.toString() ?: "")
            }

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                value = title,
                onValueChange = {title = it},
                label = { Text("Title") },
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                maxLines = 1,
                label = { Text("Page Number") },
                value = pageNumber,
                onValueChange = { newText ->
                    if (newText.all { it.isDigit() }) {
                        pageNumber = newText
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 25.sp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
            )

            ElevatedButton(
                modifier = Modifier.padding(bottom = 24.dp),
                onClick = {
                onSave(title, pageNumber.toLong())
            }
            ) {
                Text(text ="Save")
            }
        }
    }
}