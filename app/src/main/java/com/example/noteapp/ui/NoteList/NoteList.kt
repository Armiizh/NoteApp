package com.example.noteapp.ui.NoteList

import android.R
import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.noteapp.Constants
import com.example.noteapp.NoteApp
import com.example.noteapp.NotesViewModel
import com.example.noteapp.model.Note
import com.example.noteapp.model.getDay
import com.example.noteapp.model.orPlaceHolderList
import com.example.noteapp.ui.GenericAppBar
import com.example.noteapp.ui.theme.NoteAppTheme
import com.example.noteapp.ui.theme.Purple40
import com.example.noteapp.ui.theme.Purple80

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NoteListScreen(
    navController: NavController,
    viewModel: NotesViewModel,
) {


    val deleteText = remember {
        mutableStateOf("")
    }
    val noteQuery = remember {
        mutableStateOf("")
    }
    val notesToDelete = remember {
        mutableStateOf(listOf<Note>())
    }
    val openDialog = remember {
        mutableStateOf(false)
    }

    val notes = viewModel.notes.observeAsState()
    val context = LocalContext.current


    NoteAppTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colors.primary
        ) {
            Scaffold(
                topBar = {
                    GenericAppBar(
                        title = stringResource(id = com.example.noteapp.R.string.app_name),
                        onIconClick = {
                            if (notes.value?.isNotEmpty() == true) {
                                openDialog.value = true
                                deleteText.value = "Are you sure you want to delete all notes"
                                notesToDelete.value = notes.value ?: emptyList()

                            } else {
                                Toast.makeText(context, "No notes found", Toast.LENGTH_SHORT).show()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_menu_delete),
                                contentDescription = stringResource(com.example.noteapp.R.string.delete_note),
                                tint = Color.Black
                            )
                        },
                        iconState = remember {
                            mutableStateOf(true)
                        }
                    )
                },
                floatingActionButton = {
                    NotesFab(
                        contentDescription = stringResource(id = com.example.noteapp.R.string.create_note),
                        action = {
                            navController.navigate(Constants.NAVIGATION_NOTES_CREATE)
                        },
                        icon = com.example.noteapp.R.drawable.baseline_note_add_24
                    )
                }
            ) {
                Column {
                    SearchBar(query = noteQuery)
                    NoteList(
                        notes = notes.value.orPlaceHolderList(),
                        opendialog = openDialog,
                        query = noteQuery,
                        deleteText = deleteText,
                        navController = navController,
                        notesToDelete = notesToDelete
                    )
                }

                DeleteDialog(
                    opendialog = openDialog,
                    text = deleteText,
                    action = {
                             notesToDelete.value.forEach {
                                 viewModel.deleteNote(it)
                             }
                    },
                    notesToDelete = notesToDelete)
            }
        }
    }
}

@Composable
fun SearchBar(query: MutableState<String>) {

    Column(
        modifier = Modifier
            .padding(
                top = 12.dp,
                start = 12.dp,
                end = 12.dp,
                bottom = 0.dp
            )
    ) {
        TextField(
            value = query.value,
            placeholder = { Text(text = "Search..") },
            onValueChange = { query.value = it },
            modifier = Modifier
                .background(Color.White)
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Black
            ),
            trailingIcon = {
                AnimatedVisibility(
                    visible = query.value.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                )
                {
                    IconButton(onClick = { query.value = "" }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = com.example.noteapp.R.drawable.icon_cross),
                            contentDescription = stringResource(id = com.example.noteapp.R.string.clear_search)
                        )
                    }
                }
            }
        )
    }
}


@Composable
fun NoteList(
    notes: List<Note>,
    opendialog: MutableState<Boolean>,
    query: MutableState<String>,
    deleteText: MutableState<String>,
    navController: NavController,
    notesToDelete: MutableState<List<Note>>,
) {
    var previousHeader = ""

    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        modifier = Modifier.background(MaterialTheme.colors.primary)
    ) {
        val queriedNotes = if (query.value.isEmpty()) {
            notes
        } else {
            notes.filter { it.note.contains(query.value) || it.title.contains(query.value) }
        }

        itemsIndexed(queriedNotes) { index, note ->
            if (note.getDay() != previousHeader) {
                Column(
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = note.getDay()!!, color = Color.Black)
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                )
                previousHeader = note.getDay()!!
            }

            NoteListItem(
                note,
                opendialog,
                deleteText,
                navController,
                if (index % 2 == 0) {
                    Purple40
                } else {
                    Purple80
                },
                notesToDelete
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteListItem(
    note: Note,
    opendialog: MutableState<Boolean>,
    deleteText: MutableState<String>,
    navController: NavController,
    noteBackGround: Color,
    notesToDelete: MutableState<List<Note>>,
) {

    Box(
        modifier = Modifier
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier
                .background(noteBackGround)
                .height(120.dp)
                .fillMaxWidth()
                .combinedClickable(interactionSource = remember {
                    MutableInteractionSource()
                },
                    indication = rememberRipple(bounded = false),
                    onClick = {
                        if (note.id != 0) {
                            navController.navigate(
                                Constants.noteDetailNavigation(
                                    noteId = note.id ?: 0
                                )
                            )
                        }
                    },
                    onLongClick = {
                        if (note.id != 0) {
                            opendialog.value = true
                            deleteText.value = "Are you sure you want to delete this note?"
                            notesToDelete.value = mutableListOf(note)
                        }
                    }

                )
        ) {
            Row(

            ) {
                if (note.imageUri != null && note.imageUri.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model =
                            ImageRequest
                                .Builder(LocalContext.current)
                                .data(Uri.parse(note.imageUri))
                                .build()
                        ),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(0.3f),
                        contentScale = ContentScale.Crop
                    )
                }
                Column {
                    Text(
                        text = note.title,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    Text(
                        text = note.note,
                        color = Color.Black,
                        maxLines = 3,
                        modifier = Modifier.padding(12.dp)
                    )
                    Text(
                        text = note.dateUpdated,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun NotesFab(
    contentDescription: String,
    icon: Int,
    action: () -> Unit,
) {
    return FloatingActionButton(
        onClick = { action.invoke() },
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription,
            tint = Color.Black
        )
    }
}

@Composable
fun DeleteDialog(
    opendialog: MutableState<Boolean>,
    text: MutableState<String>,
    action: () -> Unit,
    notesToDelete: MutableState<List<Note>>,
) {
    if (opendialog.value) {
        AlertDialog(
            onDismissRequest = { opendialog.value = false },
            title = { Text(text = "Delete Note") },
            text = {
                Column {
                    Text(text.value)
                }
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Black,
                                contentColor = Color.White
                            ),
                            onClick = {
                                action.invoke()
                                opendialog.value = false
                                notesToDelete.value = mutableListOf()
                            }
                        ) {
                            Text(text = "Yes")
                        }
                        Spacer(modifier = Modifier.padding(12.dp))
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Black,
                                contentColor = Color.White
                            ),
                            onClick = {
                                opendialog.value = false
                                notesToDelete.value = mutableListOf()
                            }
                        ) {
                            Text(text = "No")
                        }
                    }
                }
            }

        )
    }
}




