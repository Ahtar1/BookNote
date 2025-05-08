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
import com.example.booknote.presentation.add_book.AddBookPage
import com.example.booknote.presentation.book_details.BookDetailsPage
import com.example.booknote.presentation.books.BooksPage
import com.example.booknote.presentation.calendar.CalendarPage
import com.example.booknote.presentation.draw_note.DrawNotePage
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
                            route = Page.AddNotePage.route + "?bookId={bookId}&noteId={noteId}&noteColor={noteColor}",
                            arguments = listOf(
                                navArgument(name = "bookId") {
                                    type = NavType.LongType
                                    defaultValue = -1
                                },
                                navArgument(name = "noteId") {
                                    nullable = true
                                    defaultValue = null
                                },
                                navArgument(name = "noteColor") {
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) {
                            val bookId = it.arguments?.getLong("bookId") ?: -1
                            val noteId = it.arguments?.getString("noteId")?.toLong()
                            val noteColor = it.arguments?.getLong("noteColor")
                            AddNotePage(navController, bookId, noteId, noteColor)
                        }
                        composable(
                            route = Page.NotesPage.route +
                                    "?bookId={bookId}",
                            arguments = listOf(
                                navArgument(
                                    name = "bookId"
                                ) {
                                    type = NavType.LongType
                                    defaultValue = -1
                                },
                            )
                        ){
                            val bookId = it.arguments?.getLong("bookId") ?: -1
                            NotesPage(
                                navController = navController,
                                bookId = bookId,
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
                            route = Page.DrawNotePage.route + "?bookId={bookId}&noteId={noteId}",
                            arguments = listOf(
                                navArgument(
                                    name = "bookId"
                                ) {
                                    type = NavType.LongType
                                    defaultValue = -1
                                },
                                navArgument(name = "noteId") {
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ){
                            val bookId = it.arguments?.getLong("bookId") ?: -1
                            val noteId = it.arguments?.getString("noteId")?.toLong()
                            DrawNotePage(
                                navController, bookId, noteId
                            )
                        }
                        composable(route = Page.CalendarPage.route){
                            CalendarPage(navController)
                        }
                        composable(
                            route = Page.AddBookPage.route,

                        ){
                            AddBookPage(
                                navController
                            )
                        }
                        composable(
                            route = Page.BookDetailsPage.route + "?bookId={bookId}",
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
                            BookDetailsPage(
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
