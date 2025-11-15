package com.studia.kasamate.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studia.kasamate.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _username = MutableStateFlow<String?>(null)
    val username = _username.asStateFlow()

    fun setAuthenticated(authenticated: Boolean) {
        _isAuthenticated.value = authenticated
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val userPassword = userRepository.getUser(username)
            if (userPassword != null && userPassword == password) {
                _isAuthenticated.value = true
                _username.value = username
                _loginState.value = LoginState.Success(username)
            } else {
                _loginState.value = LoginState.Error("Invalid username or password")
            }
        }
    }

    fun register(username: String, password: String, confirm: String) {
        viewModelScope.launch {
            if (password != confirm) {
                _loginState.value = LoginState.Error("Passwords do not match")
                return@launch
            }
            _loginState.value = LoginState.Loading
            val success = userRepository.addUser(username, password)
            if (success) {
                _isAuthenticated.value = true
                _username.value = username
                _loginState.value = LoginState.Success(username)
            } else {
                _loginState.value = LoginState.Error("User already exists")
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val username: String) : LoginState()
    data class Error(val message: String) : LoginState()
}
