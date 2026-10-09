package com.example.testcour.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.testcour.ui.add.AddNoteScreen
import com.example.testcour.ui.components.NotesBottomBar
import com.example.testcour.ui.detail.NoteDetailScreen
import com.example.testcour.ui.list.NoteListScreen
import com.example.testcour.ui.map.MapScreen
import com.example.testcour.ui.settings.SettingsScreen

object Routes {
    const val LIST = "notes"
    const val MAP = "map"
    const val SETTINGS = "settings"
    const val ADD = "add"
    const val NOTE_ID_ARG = "noteId"
    const val DETAIL = "note/{$NOTE_ID_ARG}"

    fun detail(id: Long) = "note/$id"
}

@Composable
fun NotesNavHost(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val bottomBar = @Composable {
        NotesBottomBar(currentRoute = currentRoute, onNavigate = navController::navigateToTab)
    }
    val openDetail: (Long) -> Unit = { navController.navigate(Routes.detail(it)) }

    NavHost(navController = navController, startDestination = Routes.LIST) {
        composable(Routes.LIST) {
            NoteListScreen(
                bottomBar = bottomBar,
                onAddClick = { navController.navigate(Routes.ADD) },
                onNoteClick = openDetail
            )
        }
        composable(Routes.MAP) {
            MapScreen(bottomBar = bottomBar, onNoteClick = openDetail)
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(bottomBar = bottomBar)
        }
        composable(Routes.ADD) {
            AddNoteScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument(Routes.NOTE_ID_ARG) { type = NavType.LongType })
        ) {
            NoteDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(Routes.LIST) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
