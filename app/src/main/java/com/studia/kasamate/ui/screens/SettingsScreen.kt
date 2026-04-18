package com.studia.kasamate.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.studia.kasamate.MainActivity
import com.studia.kasamate.R
import com.studia.kasamate.ui.theme.KasaMateTheme
import com.studia.kasamate.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = viewModel()) {
    var currency by remember { mutableStateOf(try { viewModel.getCurrency() } catch (e: Exception) { "PLN" }) }
    var language by remember { mutableStateOf(try { viewModel.getLanguage() } catch (e: Exception) { "auto" }) }
    var theme by remember { mutableStateOf(try { viewModel.getTheme() } catch (e: Exception) { "system" }) }
    var biometricAuthEnabled by remember { mutableStateOf(try { viewModel.isBiometricAuthEnabled() } catch (e: Exception) { false }) }
    var monthlyBudget by remember { mutableStateOf(try { viewModel.getMonthlyBudget().toString() } catch (e: Exception) { "0.0" }) }
    
    var currencyExpanded by remember { mutableStateOf(false) }
    var languageExpanded by remember { mutableStateOf(false) }
    var themeExpanded by remember { mutableStateOf(false) }
    val currencies = listOf("PLN", "USD", "EUR", "GBP")
    val languages = mapOf(
        "auto" to stringResource(R.string.language_auto),
        "en" to stringResource(R.string.language_en),
        "pl" to stringResource(R.string.language_pl)
    )
    val themes = mapOf("light" to stringResource(R.string.theme_light), "dark" to stringResource(R.string.theme_dark), "system" to stringResource(R.string.theme_system))
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${stringResource(R.string.currency)}: ")
                TextButton(onClick = { currencyExpanded = true }) {
                    Text(text = currency)
                }
                DropdownMenu(
                    expanded = currencyExpanded,
                    onDismissRequest = { currencyExpanded = false }
                ) {
                    currencies.forEach { curr ->
                        DropdownMenuItem(text = { Text(curr) }, onClick = {
                            currency = curr
                            viewModel.setCurrency(curr)
                            currencyExpanded = false
                        })
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${stringResource(R.string.language)}: ")
                TextButton(onClick = { languageExpanded = true }) {
                    Text(text = languages[language] ?: stringResource(R.string.language_auto))
                }
                DropdownMenu(
                    expanded = languageExpanded,
                    onDismissRequest = { languageExpanded = false }
                ) {
                    languages.forEach { (code, name) ->
                        DropdownMenuItem(text = { Text(name) }, onClick = {
                            language = code
                            viewModel.setLanguage(code)
                            languageExpanded = false
                            // Restart the app to apply language change
                            val activity = (context as? Activity)
                            activity?.finish()
                            context.startActivity(Intent(context, MainActivity::class.java))
                        })
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${stringResource(R.string.theme)}: ")
                TextButton(onClick = { themeExpanded = true }) {
                    Text(text = themes[theme] ?: stringResource(R.string.theme_system))
                }
                DropdownMenu(
                    expanded = themeExpanded,
                    onDismissRequest = { themeExpanded = false }
                ) {
                    themes.forEach { (code, name) ->
                        DropdownMenuItem(text = { Text(name) }, onClick = {
                            theme = code
                            viewModel.setTheme(code)
                            themeExpanded = false
                            // Restart the app to apply theme change
                            val activity = (context as? Activity)
                            activity?.finish()
                            context.startActivity(Intent(context, MainActivity::class.java))
                        })
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.biometric_auth)) 
                Switch(checked = biometricAuthEnabled, onCheckedChange = { 
                    biometricAuthEnabled = it
                    viewModel.setBiometricAuthEnabled(it)
                })
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = monthlyBudget,
                onValueChange = { 
                    monthlyBudget = it
                    it.toDoubleOrNull()?.let { budget ->
                        viewModel.setMonthlyBudget(budget)
                    }
                },
                label = { Text(stringResource(R.string.monthly_budget)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    KasaMateTheme {
        SettingsScreen(navController = rememberNavController())
    }
}
