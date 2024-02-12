package com.example.noteapp

import com.example.noteapp.model.Note

object Constants {

    const val TABLE_NAME = "notes"
    const val DATABASE_NAME = "notesDatabase"

    val noteDetailPlaceHolder = Note(
        note = "Cannot find note details",
        id = 0,
        title = "Cannot find note details",
    )

    const val NAVIGATION_NOTES_CREATE = "noteCreate"
    const val NAVIGATION_NOTES_LIST = "notesList"
    const val NAVIGATION_NOTES_DETAIL = "notesDetail/{noteId}"
    const val NAVIGATION_NOTES_EDIT = "notesEdit/{noteId}"
    const val NAVIGATION_NOTES_ID_ARGUMENT = "notesId"

    fun noteDetailNavigation(noteId: Int) = "noteDetail/$noteId"
    fun noteEditNavigation(noteId: Int) = "noteEdit/$noteId"


}