package com.example.testcour.data.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/** Langues proposées dans les paramètres. [tag] vide = langue du système. */
enum class AppLanguage(val tag: String) {
    SYSTEM(""),
    FRENCH("fr"),
    ENGLISH("en");

    companion object {
        fun fromTag(tag: String): AppLanguage =
            entries.firstOrNull { it.tag.isNotEmpty() && tag.startsWith(it.tag) } ?: SYSTEM
    }
}

/**
 * Lit / modifie la langue de l'application via l'API "per-app language" d'AndroidX.
 * Le choix est mémorisé automatiquement (système sur Android 13+, AppCompat avant).
 */
class LanguageRepository {

    fun getLanguage(): AppLanguage =
        AppLanguage.fromTag(AppCompatDelegate.getApplicationLocales().toLanguageTags())

    fun setLanguage(language: AppLanguage) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.tag))
    }
}
