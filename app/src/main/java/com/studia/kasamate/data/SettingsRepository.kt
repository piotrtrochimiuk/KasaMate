package com.studia.kasamate.data

import android.content.Context
import android.content.SharedPreferences

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun getCurrency(): String {
        return prefs.getString("currency", "PLN") ?: "PLN"
    }

    fun setCurrency(currency: String) {
        prefs.edit().putString("currency", currency).apply()
    }

    fun getLanguage(): String {
        return prefs.getString("language", "auto") ?: "auto"
    }

    fun setLanguage(language: String) {
        prefs.edit().putString("language", language).apply()
    }

    fun getTheme(): String {
        return prefs.getString("theme", "system") ?: "system"
    }

    fun setTheme(theme: String) {
        prefs.edit().putString("theme", theme).apply()
    }

    fun isBiometricAuthEnabled(): Boolean {
        return prefs.getBoolean("biometric_auth", false)
    }

    fun setBiometricAuthEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("biometric_auth", enabled).apply()
    }
}
