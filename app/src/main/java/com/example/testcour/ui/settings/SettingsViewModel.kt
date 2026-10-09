package com.example.testcour.ui.settings

import androidx.lifecycle.ViewModel
import com.example.testcour.data.settings.AppLanguage
import com.example.testcour.data.settings.LanguageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(private val languageRepository: LanguageRepository) : ViewModel() {

    private val _language = MutableStateFlow(languageRepository.getLanguage())
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun onLanguageSelected(language: AppLanguage) {
        if (language == _language.value) return
        _language.value = language
        // Recrée l'activité avec la nouvelle langue
        languageRepository.setLanguage(language)
    }
}
