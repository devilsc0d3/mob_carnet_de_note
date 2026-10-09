package com.example.testcour.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource
import com.example.testcour.R
import com.example.testcour.ui.AppViewModelProvider
import com.example.testcour.ui.components.NotesMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    bottomBar: @Composable () -> Unit,
    onNoteClick: (Long) -> Unit,
    viewModel: MapViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val notes by viewModel.locatedNotes.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.map_title)) }) },
        bottomBar = bottomBar
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            NotesMap(
                notes = notes,
                zoom = 12.0,
                onNoteClick = onNoteClick,
                modifier = Modifier.fillMaxSize()
            )
            if (notes.isEmpty()) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = 3.dp,
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                ) {
                    Text(
                        stringResource(R.string.map_empty),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}
