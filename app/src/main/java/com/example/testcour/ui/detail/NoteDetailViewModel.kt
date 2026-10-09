package com.example.testcour.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testcour.data.local.NoteEntity
import com.example.testcour.data.repository.NoteRepository
import com.example.testcour.ui.navigation.Routes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: NoteRepository
) : ViewModel() {

    private val noteId: Long = checkNotNull(savedStateHandle[Routes.NOTE_ID_ARG])

    val note: StateFlow<NoteEntity?> = repository.getNote(noteId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun deleteNote(onDeleted: () -> Unit) {
        val current = note.value ?: return
        viewModelScope.launch {
            repository.deleteNote(current)
            onDeleted()
        }
    }
}
