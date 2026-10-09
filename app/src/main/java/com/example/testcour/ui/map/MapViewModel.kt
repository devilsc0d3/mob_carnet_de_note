package com.example.testcour.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testcour.data.local.NoteEntity
import com.example.testcour.data.repository.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MapViewModel(repository: NoteRepository) : ViewModel() {

    /** Uniquement les notes qui possèdent une position. */
    val locatedNotes: StateFlow<List<NoteEntity>> = repository.getNotes()
        .map { notes -> notes.filter { it.hasLocation } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
