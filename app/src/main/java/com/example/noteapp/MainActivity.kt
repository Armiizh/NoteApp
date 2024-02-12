package com.example.noteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.noteapp.ui.NoteCreate.CreateNoteScreen
import com.example.noteapp.ui.NoteDetail.NoteDetailPage
import com.example.noteapp.ui.NoteEdit.NoteEditScreen
import com.example.noteapp.ui.NoteList.NoteListScreen


class MainActivity : ComponentActivity() {

    private lateinit var viewModel: NotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = NoteViewModelFactory(NoteApp.getDao()).create(NotesViewModel::class.java)

        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Constants.NAVIGATION_NOTES_LIST
            ) {
                //Note list
                composable(Constants.NAVIGATION_NOTES_LIST) {
                    NoteListScreen(
                        navController = navController,
                        viewModel = viewModel
                    )
                }

                //note detail page
                composable(
                    Constants.NAVIGATION_NOTES_DETAIL,
                    arguments = listOf(navArgument(Constants.NAVIGATION_NOTES_ID_ARGUMENT) {
                        type = NavType.IntType
                    })
                ) { navBackStackEntry ->
                    navBackStackEntry.arguments?.getInt(Constants.NAVIGATION_NOTES_ID_ARGUMENT)
                        ?.let {
                            NoteDetailPage(
                                noteId = it,
                                navController = navController,
                                viewModel = viewModel
                            )
                        }
                }

                //note edit page
                composable(
                    Constants.NAVIGATION_NOTES_EDIT,
                    arguments = listOf(navArgument(Constants.NAVIGATION_NOTES_ID_ARGUMENT) {
                        type = NavType.IntType
                    })
                ) { navBackStackEntry ->
                    navBackStackEntry.arguments?.getInt(Constants.NAVIGATION_NOTES_ID_ARGUMENT)
                        ?.let {
                            NoteEditScreen(
                                noteId = it,
                                navController = navController,
                                viewModel = viewModel
                            )
                        }
                }

                //note create page
                composable(Constants.NAVIGATION_NOTES_CREATE) {
                    CreateNoteScreen(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

