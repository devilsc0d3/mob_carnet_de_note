package com.example.testcour.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/** Date au format de la langue donnée (ex. « 8 oct. 2026, 14:38 » / « Oct 8, 2026, 2:38 PM »). */
fun formatDate(millis: Long, locale: Locale): String =
    Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).withLocale(locale))

/** Langue actuellement affichée (suit le choix fait dans les paramètres). */
@Composable
@ReadOnlyComposable
fun currentLocale(): Locale = LocalConfiguration.current.locales[0]

fun Context.currentLocale(): Locale = resources.configuration.locales[0]

fun formatCoordinates(latitude: Double, longitude: Double): String =
    String.format(Locale.US, "%.5f, %.5f", latitude, longitude)
