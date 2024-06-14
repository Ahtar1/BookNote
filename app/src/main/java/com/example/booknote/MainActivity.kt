package com.example.booknote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.booknote.presentation.books.BooksPage
import com.example.booknote.presentation.notes.NotesPage
import com.example.booknote.presentation.util.Page
import com.example.booknote.ui.theme.BookNoteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Page.BooksPage.route
                    ){
                        composable(route = Page.BooksPage.route){
                            BooksPage(navController)
                        }
                        composable(
                            route = Page.NotesPage.route +
                                    "?bookId={bookId}&bookTitle={bookTitle}",
                            arguments = listOf(
                                navArgument(
                                    name = "bookId"
                                ) {
                                    type = NavType.LongType
                                    defaultValue = -1
                                },
                                navArgument(
                                    name = "bookTitle"
                                ) {
                                    type = NavType.StringType
                                    defaultValue = ""
                                }
                            )
                        ){
                            val bookId = it.arguments?.getLong("bookId") ?: -1
                            val bookTitle = it.arguments?.getString("bookTitle") ?: ""
                            NotesPage(
                                navController = navController,
                                bookId = bookId,
                                bookTitle = bookTitle
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BookNoteTheme {

    }
}
