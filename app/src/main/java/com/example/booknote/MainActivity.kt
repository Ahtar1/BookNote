package com.example.booknote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.booknote.ui.theme.BookNoteTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookNoteTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(title = {
                                Text(text = "Kitaplarım")
                            })
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {},
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
                                        println("Search on: $newQuery")
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
                            BooksGrid()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BooksGrid() {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        content = {
            items(16) {
                Image(
                    modifier = Modifier.size(150.dp),
                    painter = painterResource(id = R.drawable.blue_book),
                    contentDescription = "Books Grid",
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BookNoteTheme {
        BooksGrid()
    }
}
