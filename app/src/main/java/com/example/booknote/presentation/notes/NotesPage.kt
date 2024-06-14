package com.example.booknote.presentation.notes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesPage(
    navController: NavController,
    bookId: Long,
    bookTitle: String
){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                Text(text = bookTitle)
            },
                navigationIcon = {
                    IconButton(onClick = {navController.navigateUp()}) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                },
                shape = CircleShape,
                content = { Icon(Icons.Filled.Add, contentDescription = "Add") },
            )
        },
    ) { paddingValues ->
        paddingValues
        Column(
            modifier = Modifier.padding(top = 56.dp),
        ) {
            var query by remember { mutableStateOf("") }
            var active by remember { mutableStateOf(false) }
            Row {
                SearchBar(
                    modifier = Modifier.padding(start = 16.dp),
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { newQuery ->

                    },
                    active = false,
                    onActiveChange = { active = it },
                    placeholder = {
                        Text(text = "Search")
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "searchIcon")
                    },
                    content = {},
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Sort Icon",
                )
            }


        }
    }
}