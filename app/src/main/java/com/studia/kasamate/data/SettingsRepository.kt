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

    fun getMonthlyBudget(): Double {
        return prefs.getFloat("monthly_budget", 0.0f).toDouble()
    }

    fun setMonthlyBudget(budget: Double) {
        prefs.edit().putFloat("monthly_budget", budget.toFloat()).apply()
    }

    fun setRememberMe(enabled: Boolean) {
        prefs.edit().putBoolean("remember_me", enabled).apply()
    }

    fun isRememberMeEnabled(): Boolean {
        return prefs.getBoolean("remember_me", false)
    }

    fun saveLoginCredentials(username: String, password: String) {
        prefs.edit()
            .putString("saved_username", username)
            .putString("saved_password", password)
            .apply()
    }

    fun getSavedUsername(): String {
        return prefs.getString("saved_username", "") ?: ""
    }

    fun getSavedPassword(): String {
        return prefs.getString("saved_password", "") ?: ""
    }

    fun clearLoginCredentials() {
        prefs.edit()
            .remove("saved_username")
            .remove("saved_password")
            .apply()
    }
}
