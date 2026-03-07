package com.studia.kasamate

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.studia.kasamate.data.AppDatabase
import com.studia.kasamate.data.SettingsRepository
import com.studia.kasamate.data.UserRepository
import com.studia.kasamate.ui.login.LoginScreen
import com.studia.kasamate.ui.login.LoginState
import com.studia.kasamate.ui.login.LoginViewModel
import com.studia.kasamate.ui.login.LoginViewModelFactory
import com.studia.kasamate.ui.login.RegistrationScreen
import com.studia.kasamate.ui.screens.AboutScreen
import com.studia.kasamate.ui.screens.SettingsScreen
import com.studia.kasamate.ui.screens.TransactionScreen
import com.studia.kasamate.ui.theme.KasaMateTheme
import java.util.Locale

class MainActivity : FragmentActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        val userRepository = UserRepository(database.userDao(), database.transactionDao())

        loginViewModel = viewModels<LoginViewModel> { LoginViewModelFactory(userRepository) }.value

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
            loginViewModel.setAuthenticated(true)
        }

        setContent {
            val isAuthenticated by loginViewModel.isAuthenticated.collectAsState()
            if (isAuthenticated) {
                KasaMateTheme {
                    val navController = rememberNavController()
                    val loginState by loginViewModel.loginState.collectAsState()
                    val allUsers by loginViewModel.allUsers.collectAsState()

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(
                                onLoginClick = { username, password ->
                                    loginViewModel.login(username, password)
                                },
                                onRegisterClick = { navController.navigate("register") },
                                onSettingsClick = { navController.navigate("settings") },
                                onAboutClick = { navController.navigate("about") },
                                allUsers = allUsers,
                                onDeleteUser = { username, password, onSuccess, onError ->
                                    loginViewModel.deleteUser(username, password, onSuccess, { msg ->
                                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                                        onError(msg)
                                    })
                                },
                                onChangePassword = { username, oldPassword, newPassword, onSuccess, onError ->
                                    loginViewModel.changePassword(username, oldPassword, newPassword, onSuccess, { msg ->
                                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                                        onError(msg)
                                    })
                                }
                            )
                        }
                        composable("register") {
                            RegistrationScreen(
                                onRegisterClick = { username, password, confirm ->
                                    loginViewModel.register(username, password, confirm)
                                },
                                onBackPressed = { navController.popBackStack() },
                                onSettingsClick = { navController.navigate("settings") },
                                onAboutClick = { navController.navigate("about") }
                            )
                        }
                        composable(
                            "transactions/{username}",
                            arguments = listOf(navArgument("username") { type = NavType.StringType })
                        ) { backStackEntry ->
                            backStackEntry.arguments?.getString("username")?.let { username ->
                                TransactionScreen(
                                    navController = navController,
                                    username = username,
                                    onLogout = {
                                        loginViewModel.logout()
                                        navController.navigate("login") {
                                            popUpTo("transactions/$username") { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                        composable("settings") { SettingsScreen(navController = navController) }
                        composable("about") { AboutScreen(navController = navController) }
                    }

                    LaunchedEffect(loginState) {
                        when (val state = loginState) {
                            is LoginState.Success -> {
                                if (navController.currentDestination?.route?.startsWith("transactions") == false) {
                                    navController.navigate("transactions/${state.username}") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                            is LoginState.Error -> {
                                Toast.makeText(applicationContext, state.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
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
                        loginViewModel.setAuthenticated(true)
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
            loginViewModel.setAuthenticated(true) // No biometrics, allow access
        }
    }

    @SuppressLint("AppBundleLocaleChanges")
    private fun setLocale(context: Context, languageCode: String) {
        if (languageCode == "auto") {
            return
        }
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
