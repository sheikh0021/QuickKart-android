package com.application.quickkartcustomer.presentation.auth.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartcustomer.core.util.PreferencesManager
import com.application.quickkartcustomer.domain.model.AuthResponse
import com.application.quickkartcustomer.domain.usecase.AuthUseCase
import com.application.quickkartcustomer.domain.usecase.ProfileUseCase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authUseCase: AuthUseCase,
                                         private val profileUseCase: ProfileUseCase,
    private val preferencesManager: PreferencesManager): ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _loginResult = MutableLiveData<Result<AuthResponse>>()
    val loginResult: LiveData<Result<AuthResponse>> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            authUseCase.login(username, password).fold(
                onSuccess = {authResponse ->
                    sendFcmTokenToBackend()
                    //save token and user data
                    preferencesManager.saveToken(authResponse.tokens.access)
                    preferencesManager.saveUser(authResponse.user)
                    _loginState.value = LoginState.Success
                },
                onFailure = {exception ->
                    _loginState.value = LoginState.Error(exception.message ?: "Login failed")
                }
            )
        }
    }
    private fun sendFcmTokenToBackend() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result

            viewModelScope.launch {
                try {
                    profileUseCase.updateFcmToken(token)
                } catch (e: Exception) {
                    Log.e("FCM", "Failed to send token: ${e.message}")
                }
            }
        }
    }
}

sealed class LoginState{
    object Idle: LoginState()
    object Loading: LoginState()
    object Success: LoginState()
    data class Error(val message: String) : LoginState()
}