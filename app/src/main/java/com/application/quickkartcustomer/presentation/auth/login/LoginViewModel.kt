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
import kotlinx.coroutines.tasks.await
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
                    //save token and user data first
                    preferencesManager.saveToken(authResponse.tokens.access)
                    preferencesManager.saveUser(authResponse.user)

                    // Send FCM token synchronously before marking login as success
                    try {
                        sendFcmTokenSync()
                        _loginState.value = LoginState.Success
                    } catch (e: Exception) {
                        // Even if FCM fails, login should succeed
                        Log.e("FCM", "Failed to send FCM token, but login successful", e)
                        _loginState.value = LoginState.Success
                    }
                },
                onFailure = {exception ->
                    _loginState.value = LoginState.Error(exception.message ?: "Login failed")
                }
            )
        }
    }

    private suspend fun sendFcmTokenSync() {
        var lastException: Exception? = null

        // Retry FCM token retrieval up to 3 times with exponential backoff
        for (attempt in 1..3) {
            try {
                Log.d("FCM", "Attempting FCM token retrieval (attempt $attempt)")
                val token = FirebaseMessaging.getInstance().token.await()
                profileUseCase.updateFcmToken(token)
                Log.d("FCM", "FCM token sent to backend successfully")
                return // Success, exit function
            } catch (e: Exception) {
                Log.e("FCM", "FCM token attempt $attempt failed: ${e.message}")
                lastException = e

                // If it's a SERVICE_NOT_AVAILABLE error, don't retry (Google Play Services issue)
                if (e.message?.contains("SERVICE_NOT_AVAILABLE") == true) {
                    Log.w("FCM", "Google Play Services not available - FCM will not work on this device")
                    break
                }

                // Wait before retry (exponential backoff)
                if (attempt < 3) {
                    kotlinx.coroutines.delay(1000L * attempt) // 1s, 2s, 3s
                }
            }
        }

        // If we get here, all attempts failed
        Log.e("FCM", "All FCM token retrieval attempts failed")
        throw lastException ?: Exception("FCM token retrieval failed")
    }
}

sealed class LoginState{
    object Idle: LoginState()
    object Loading: LoginState()
    object Success: LoginState()
    data class Error(val message: String) : LoginState()
}