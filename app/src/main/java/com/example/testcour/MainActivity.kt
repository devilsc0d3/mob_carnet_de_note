package com.example.testcour

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.testcour.ui.navigation.NotesNavHost
import com.example.testcour.ui.theme.TestCourTheme

// AppCompatActivity (et non ComponentActivity) pour que le changement de langue
// via AppCompatDelegate.setApplicationLocales fonctionne aussi avant Android 13.
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestCourTheme {
                NotesNavHost()
            }
        }
    }
}
