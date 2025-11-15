package com.studia.kasamate.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.studia.kasamate.data.SettingsRepository

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SettingsRepository = SettingsRepository(application)

    fun getCurrency(): String {
        return repository.getCurrency()
    }

    fun setCurrency(currency: String) {
        repository.setCurrency(currency)
    }

    fun getLanguage(): String {
        return repository.getLanguage()
    }

    fun setLanguage(language: String) {
        repository.setLanguage(language)
    }

    fun getTheme(): String {
        return repository.getTheme()
    }

    fun setTheme(theme: String) {
        repository.setTheme(theme)
    }

    fun isBiometricAuthEnabled(): Boolean {
        return repository.isBiometricAuthEnabled()
    }

    fun setBiometricAuthEnabled(enabled: Boolean) {
        repository.setBiometricAuthEnabled(enabled)
    }
}
