package com.example.booknote.presentation.notes.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.booknote.domain.util.SortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    onDismissRequest: () -> Unit,
    sortItems: List<ToggleItem>,
    initialSelectedItem: ToggleItem?,
    onItemSelected: (ToggleItem) -> Unit,
    onClick: (order: SortOrder) -> Unit
){
    var selectedItem by remember { mutableStateOf<ToggleItem?>(null) }

    selectedItem = initialSelectedItem

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
    ) {

        LazyColumn {
            items(sortItems) { item ->
                ToggleListItem(item, selectedItem?.id) { selectedId ->
                    selectedItem = sortItems.find { it.id == selectedId }

                    sortItems.forEach { it.isChecked = it.id == selectedItem?.id }
                }
            }
        }
        ElevatedButton(
            onClick = {
                selectedItem?.let { onClick(it.sortOrder) }
                onItemSelected(selectedItem!!)
            },
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Sort")
        }
    }
}

data class ToggleItem(
    val id: Int,
    val title: String,
    val sortOrder: SortOrder,
    var isChecked: Boolean = false,
)
