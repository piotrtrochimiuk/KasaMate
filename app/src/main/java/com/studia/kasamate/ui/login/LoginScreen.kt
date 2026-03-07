package com.studia.kasamate.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.studia.kasamate.R
import com.studia.kasamate.data.User
import com.studia.kasamate.ui.theme.KasaMateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    allUsers: List<User>,
    onDeleteUser: (String, String, () -> Unit, (String) -> Unit) -> Unit,
    onChangePassword: (String, String, String, () -> Unit, (String) -> Unit) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var showUserManagement by remember { mutableStateOf(false) }

    var userToDelete by remember { mutableStateOf<String?>(null) }
    var userToChangePassword by remember { mutableStateOf<String?>(null) }
    var confirmPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.login)) },
                actions = {
                    IconButton(onClick = { showUserManagement = true }) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.manage_users))
                    }
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.settings))
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.settings)) },
                            onClick = {
                                showMenu = false
                                onSettingsClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.about)) },
                            onClick = {
                                showMenu = false
                                onAboutClick()
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (showUserManagement) {
            AlertDialog(
                onDismissRequest = { showUserManagement = false },
                title = { Text(stringResource(R.string.manage_users)) },
                text = {
                    LazyColumn {
                        items(allUsers) { user ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(user.username)
                                Row {
                                    IconButton(onClick = { userToChangePassword = user.username }) {
                                        Icon(Icons.Default.Edit, contentDescription = null)
                                    }
                                    IconButton(onClick = { userToDelete = user.username }) {
                                        Icon(Icons.Default.Delete, contentDescription = null)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showUserManagement = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

        if (userToDelete != null) {
            AlertDialog(
                onDismissRequest = { userToDelete = null },
                title = { Text(stringResource(R.string.confirm_delete_user)) },
                text = {
                    Column {
                        Text("${stringResource(R.string.enter_password_to_confirm)} ${userToDelete}:")
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text(stringResource(R.string.password)) },
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        onDeleteUser(userToDelete!!, confirmPassword, {
                            userToDelete = null
                            confirmPassword = ""
                        }, { /* error handled by toast in main if needed */ })
                    }) {
                        Text(stringResource(R.string.delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { userToDelete = null }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

        if (userToChangePassword != null) {
            AlertDialog(
                onDismissRequest = { userToChangePassword = null },
                title = { Text(stringResource(R.string.change_password)) },
                text = {
                    Column {
                        Text("${stringResource(R.string.change_password)} ${userToChangePassword}:")
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text(stringResource(R.string.old_password)) },
                            visualTransformation = PasswordVisualTransformation()
                        )
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text(stringResource(R.string.new_password)) },
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        onChangePassword(userToChangePassword!!, confirmPassword, newPassword, {
                            userToChangePassword = null
                            confirmPassword = ""
                            newPassword = ""
                        }, { /* error */ })
                    }) {
                        Text(stringResource(R.string.update))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { userToChangePassword = null }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(R.string.username)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onLoginClick(username, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.login))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onRegisterClick() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.register))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    KasaMateTheme {
        LoginScreen(
            onLoginClick = { _, _ -> },
            onRegisterClick = {},
            onSettingsClick = {},
            onAboutClick = {},
            allUsers = emptyList(),
            onDeleteUser = { _, _, _, _ -> },
            onChangePassword = { _, _, _, _, _ -> }
        )
    }
}
