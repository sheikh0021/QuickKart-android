package com.application.quickkartadmin.presentation.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartadmin.core.util.PreferencesManager
import com.application.quickkartadmin.domain.usecase.AdminUseCase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@HiltViewModel
class AdminLoginViewModel @Inject constructor(
    private val adminUseCase: AdminUseCase,
    private val preferencesManager: PreferencesManager
): ViewModel() {

    private suspend fun sendFcmTokenSync() {
        var lastException: Exception? = null

        // Retry FCM token retrieval up to 3 times with exponential backoff
        for (attempt in 1..3) {
            try {
                Log.d("FCM", "Admin FCM token retrieval attempt $attempt")
                val fcmToken = FirebaseMessaging.getInstance().token.await()
                adminUseCase.updateFcmToken(fcmToken).fold(
                    onSuccess = {
                        Log.d("FCM", "FCM token sent to backend successfully after login")
                        return // Success, exit function
                    },
                    onFailure = { exception ->
                        Log.e("FCM", "Failed to send FCM token after login: ${exception.message}")
                        lastException = exception as Exception?
                    }
                )
            } catch (e: Exception) {
                Log.e("FCM", "Admin FCM token attempt $attempt failed: ${e.message}")
                lastException = e

                // If it's a SERVICE_NOT_AVAILABLE error, don't retry
                if (e.message?.contains("SERVICE_NOT_AVAILABLE") == true) {
                    Log.w("FCM", "Google Play Services not available - FCM will not work on this device")
                    break
                }

                // Wait before retry (exponential backoff)
                if (attempt < 3) {
                    kotlinx.coroutines.delay(1000L * attempt)
                }
            }
        }

        // If we get here, all attempts failed
        Log.e("FCM", "All admin FCM token retrieval attempts failed")
        throw lastException ?: Exception("Admin FCM token retrieval failed")
    }
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        // Check if user is already logged in on app startup
        _isLoggedIn.value = preferencesManager.isLoggedIn()
    }
    fun onUsernameChange(username: String) {
        _username.value = username
        _error.value = null
    }
    fun onPasswordChange(password: String) {
        _password.value = password
        _error.value = null
    }

    fun login() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            adminUseCase.login(_username.value, _password.value).fold(
                onSuccess = {admin ->
                    preferencesManager.saveToken(admin.token)
                    preferencesManager.saveUserData(
                        mapOf(
                            "id" to admin.user.id,
                            "username" to admin.user.username,
                            "full_name" to admin.user.fullName,
                            "user_type" to admin.user.userType
                        )
                    )

                    // Send FCM token synchronously before marking as logged in
                    viewModelScope.launch {
                        try {
                            sendFcmTokenSync()
                            _isLoggedIn.value = true
                        } catch (e: Exception) {
                            // Even if FCM fails, login should succeed
                            Log.e("FCM", "Failed to send FCM token, but login successful", e)
                            _isLoggedIn.value = true
                        }
                    }
                },
                onFailure = {exception ->
                    _error.value = exception.message ?: "Login failed"
                }
            )
            _isLoading.value = false
        }
    }

    fun logout() {
        preferencesManager.clearAllData()
        _isLoggedIn.value = false
        _username.value = ""
        _password.value = ""
        _error.value = null
    }
}