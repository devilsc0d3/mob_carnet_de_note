package com.example.testcour.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.testcour.R
import com.example.testcour.ui.navigation.Routes

private data class Tab(val route: String, @StringRes val label: Int, val icon: ImageVector)

private val tabs = listOf(
    Tab(Routes.LIST, R.string.tab_notes, Icons.AutoMirrored.Filled.List),
    Tab(Routes.MAP, R.string.tab_map, Icons.Filled.Place),
    Tab(Routes.SETTINGS, R.string.tab_settings, Icons.Filled.Settings)
)

@Composable
fun NotesBottomBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    NavigationBar {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = { if (currentRoute != tab.route) onNavigate(tab.route) },
                icon = { Icon(tab.icon, contentDescription = null) },
                label = { Text(stringResource(tab.label)) }
            )
        }
    }
}
