package com.example.booknote.presentation.notes.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ToggleListItem(
    item: ToggleItem,
    selectedItemId: Int?,
    onCheckedChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onCheckedChange(item.id) }, // Row'a clickable ekleyin
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.title,
            modifier = Modifier.weight(1f)
        )

        Checkbox(
            checked = item.id == selectedItemId,
            onCheckedChange = { onCheckedChange(item.id) }
        )
    }
}
