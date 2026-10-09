package com.example.testcour

import android.app.Application
import com.example.testcour.data.local.AppDatabase
import com.example.testcour.data.location.LocationProvider
import com.example.testcour.data.photo.PhotoStorage
import com.example.testcour.data.repository.NoteRepository
import com.example.testcour.data.settings.LanguageRepository

/** Point d'entrée de l'application : sert de conteneur de dépendances (injection manuelle). */
class NotesApplication : Application() {

    val repository by lazy { NoteRepository(AppDatabase.getInstance(this).noteDao()) }
    val locationProvider by lazy { LocationProvider(this) }
    val photoStorage by lazy { PhotoStorage(this) }
    val languageRepository by lazy { LanguageRepository() }
}
