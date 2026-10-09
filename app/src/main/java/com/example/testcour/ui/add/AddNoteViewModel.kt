package com.example.testcour.ui.add

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.example.testcour.R
import androidx.lifecycle.viewModelScope
import com.example.testcour.data.local.NoteEntity
import com.example.testcour.data.location.LocationProvider
import com.example.testcour.data.photo.PhotoStorage
import com.example.testcour.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class AddNoteUiState(
    val title: String = "",
    val content: String = "",
    val photoPath: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isLocating: Boolean = false,
    /** Message d'erreur (id de ressource, traduit par l'écran). */
    @StringRes val locationError: Int? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
) {
    val canSave: Boolean get() = title.isNotBlank() && !isSaving
}

class AddNoteViewModel(
    private val repository: NoteRepository,
    private val locationProvider: LocationProvider,
    private val photoStorage: PhotoStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddNoteUiState())
    val uiState: StateFlow<AddNoteUiState> = _uiState.asStateFlow()

    /** Fichier en attente pendant que l'appareil photo est ouvert. */
    private var pendingPhoto: File? = null

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }

    fun onContentChange(value: String) = _uiState.update { it.copy(content = value) }

    // --- Photo ---

    fun createCameraUri(): Uri {
        val (file, uri) = photoStorage.createPhotoFile()
        pendingPhoto = file
        return uri
    }

    fun onPhotoTaken(success: Boolean) {
        val file = pendingPhoto ?: return
        pendingPhoto = null
        if (success && file.length() > 0) replacePhoto(file.absolutePath) else file.delete()
    }

    fun onGalleryImagePicked(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            photoStorage.importFromUri(uri)?.let { replacePhoto(it.absolutePath) }
        }
    }

    fun removePhoto() = replacePhoto(null)

    private fun replacePhoto(newPath: String?) {
        _uiState.value.photoPath?.let(photoStorage::delete)
        _uiState.update { it.copy(photoPath = newPath) }
    }

    // --- Localisation ---

    fun fetchLocation() {
        if (!locationProvider.hasPermission()) {
            _uiState.update { it.copy(locationError = R.string.error_location_permission) }
            return
        }
        _uiState.update { it.copy(isLocating = true, locationError = null) }
        viewModelScope.launch {
            val location = locationProvider.getCurrentLocation()
            _uiState.update {
                if (location != null) {
                    it.copy(isLocating = false, latitude = location.latitude, longitude = location.longitude)
                } else {
                    it.copy(isLocating = false, locationError = R.string.error_location_unavailable)
                }
            }
        }
    }

    // --- Enregistrement ---

    fun save() {
        val state = _uiState.value
        if (!state.canSave) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            repository.addNote(
                NoteEntity(
                    title = state.title.trim(),
                    content = state.content.trim(),
                    photoPath = state.photoPath,
                    date = System.currentTimeMillis(),
                    latitude = state.latitude,
                    longitude = state.longitude
                )
            )
            _uiState.update { it.copy(isSaving = false, isSaved = true) }
        }
    }

    override fun onCleared() {
        // Note abandonnée : on supprime la photo orpheline
        val state = _uiState.value
        if (!state.isSaved && !state.isSaving) state.photoPath?.let(photoStorage::delete)
        pendingPhoto?.delete()
    }
}
