package com.studia.kasamate.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.studia.kasamate.data.SettingsRepository

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun KasaMateTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val settingsRepository = remember { SettingsRepository(context) }
    val darkTheme = isDarkTheme(settingsRepository.getTheme())

    val colorScheme = when {
        true -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun isDarkTheme(theme: String): Boolean {
    return when (theme) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
}
