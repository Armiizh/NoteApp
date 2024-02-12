package com.example.noteapp

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.room.Room
import com.example.noteapp.persistence.NotesDao
import com.example.noteapp.persistence.NotesDatabase

class NoteApp: Application() {
    private var db: NotesDatabase? = null

    init {
        instance = this
    }

    private fun getDb(): NotesDatabase {
        return if (db != null) {
            db!!
        } else {
            db = Room.databaseBuilder(
                instance!!.applicationContext,
                NotesDatabase::class.java,
                Constants.DATABASE_NAME
            ).build()
            db!!
        }
    }


    companion object {
        private var instance: NoteApp? = null

        fun getDao(): NotesDao {
            return instance!!.getDb().NotesDao()
        }

        fun getUriPermission(uri: Uri) {
            instance!!.applicationContext.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }
}