package com.example.testcour.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource
import com.example.testcour.R
import com.example.testcour.data.local.NoteEntity
import com.example.testcour.ui.AppViewModelProvider
import com.example.testcour.ui.components.NotePhoto
import com.example.testcour.ui.components.NotesMap
import com.example.testcour.ui.components.currentLocale
import com.example.testcour.ui.components.formatCoordinates
import com.example.testcour.ui.components.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    onBack: () -> Unit,
    viewModel: NoteDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val note by viewModel.note.collectAsStateWithLifecycle()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }, enabled = note != null) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.detail_delete))
                    }
                }
            )
        }
    ) { padding ->
        val current = note
        if (current == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            NoteDetailContent(current, Modifier.padding(padding))
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.detail_delete_title)) },
            text = { Text(stringResource(R.string.detail_delete_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteNote(onDeleted = onBack)
                }) { Text(stringResource(R.string.detail_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun NoteDetailContent(note: NoteEntity, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (note.photoPath != null) {
            NotePhoto(
                photoPath = note.photoPath,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            )
        }
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(note.title, style = MaterialTheme.typography.headlineMedium)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(formatDate(note.date, currentLocale()), style = MaterialTheme.typography.labelLarge)
            }

            if (note.content.isNotBlank()) {
                Text(note.content, style = MaterialTheme.typography.bodyLarge)
            }

            OutlinedCard(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (note.hasLocation) formatCoordinates(note.latitude!!, note.longitude!!)
                        else stringResource(R.string.detail_no_location),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (note.hasLocation) {
                    NotesMap(
                        notes = listOf(note),
                        zoom = 16.0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                }
            }
        }
    }
}
