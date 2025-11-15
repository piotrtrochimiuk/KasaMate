package com.studia.kasamate

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.studia.kasamate.data.SettingsRepository
import com.studia.kasamate.ui.screens.AboutScreen
import com.studia.kasamate.ui.screens.SettingsScreen
import com.studia.kasamate.ui.screens.TransactionScreen
import com.studia.kasamate.ui.theme.KasaMateTheme
import java.util.Locale

class MainActivity : FragmentActivity() {
    private var isAuthenticated by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsRepository = SettingsRepository(this)
        setLocale(this, settingsRepository.getLanguage())

        val isSystemInDarkTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val isDarkTheme = when (settingsRepository.getTheme()) {
            "light" -> false
            "dark" -> true
            else -> isSystemInDarkTheme
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { isDarkTheme },
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { isDarkTheme }
        )

        if (settingsRepository.isBiometricAuthEnabled()) {
            showBiometricPrompt()
        } else {
            isAuthenticated = true
        }

        setContent {
            if (isAuthenticated) {
                KasaMateTheme {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "transactions") {
                        composable("transactions") { TransactionScreen(navController = navController) }
                        composable("settings") { SettingsScreen(navController = navController) }
                        composable("about") { AboutScreen(navController = navController) }
                    }
                }
            }
        }
    }

    private fun showBiometricPrompt() {
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS) {
            val executor = ContextCompat.getMainExecutor(this)
            val biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        isAuthenticated = true
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        finish()
                    }

                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setNegativeButtonText(getString(R.string.biometric_cancel))
                .build()

            biometricPrompt.authenticate(promptInfo)
        } else {
            isAuthenticated = true // No biometrics, allow access
        }
    }

    @SuppressLint("AppBundleLocaleChanges")
    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
