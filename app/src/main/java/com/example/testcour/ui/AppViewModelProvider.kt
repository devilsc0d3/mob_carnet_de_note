package com.example.testcour.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.testcour.NotesApplication
import com.example.testcour.ui.add.AddNoteViewModel
import com.example.testcour.ui.detail.NoteDetailViewModel
import com.example.testcour.ui.list.NoteListViewModel
import com.example.testcour.ui.map.MapViewModel
import com.example.testcour.ui.settings.SettingsViewModel

/** Fabrique unique de tous les ViewModels de l'application. */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { NoteListViewModel(app().repository) }
        initializer { MapViewModel(app().repository) }
        initializer { NoteDetailViewModel(createSavedStateHandle(), app().repository) }
        initializer {
            AddNoteViewModel(app().repository, app().locationProvider, app().photoStorage)
        }
        initializer { SettingsViewModel(app().languageRepository) }
    }

    private fun CreationExtras.app(): NotesApplication = this[APPLICATION_KEY] as NotesApplication
}
