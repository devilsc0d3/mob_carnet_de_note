package com.example.testcour.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testcour.data.local.NoteEntity
import com.example.testcour.data.repository.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class NoteListViewModel(repository: NoteRepository) : ViewModel() {

    val notes: StateFlow<List<NoteEntity>> = repository.getNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
