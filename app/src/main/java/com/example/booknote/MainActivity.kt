package com.example.booknote

import AddNotePage
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.booknote.presentation.add_audio.AddAudioPage
import com.example.booknote.presentation.add_image.AddImagePage
import com.example.booknote.presentation.books.BooksPage
import com.example.booknote.presentation.notes.NotesPage
import com.example.booknote.presentation.util.Page
import com.example.booknote.ui.theme.BookNoteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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
                            route = Page.AddNotePage.route + "?bookId={bookId}&noteId={noteId}",
                            arguments = listOf(
                                navArgument(name = "bookId") {
                                    type = NavType.LongType
                                    defaultValue = -1
                                },
                                navArgument(name = "noteId") {
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) {
                            val bookId = it.arguments?.getLong("bookId") ?: -1
                            val noteId = it.arguments?.getString("noteId")?.toLong()
                            AddNotePage(navController, bookId, noteId)
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
                        composable(
                            route= Page.AddAudioPage.route +
                                    "?bookId={bookId}",
                            arguments = listOf(
                                navArgument(
                                    name = "bookId"
                                ) {
                                    type = NavType.LongType
                                    defaultValue = -1
                                }
                            )
                        ){
                            val bookId = it.arguments?.getLong("bookId") ?: -1
                            AddAudioPage(
                                navController, bookId
                            )
                        }
                        composable(
                            route= Page.AddImagePage.route +
                                    "?bookId={bookId}",
                            arguments = listOf(
                                navArgument(
                                    name = "bookId"
                                ) {
                                    type = NavType.LongType
                                    defaultValue = -1
                                }
                            )
                        ){
                            val bookId = it.arguments?.getLong("bookId") ?: -1
                            AddImagePage(
                                navController, bookId
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
