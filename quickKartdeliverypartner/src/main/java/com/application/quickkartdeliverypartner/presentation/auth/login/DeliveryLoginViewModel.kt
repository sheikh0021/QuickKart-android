package com.application.quickkartdeliverypartner.presentation.auth.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartdeliverypartner.core.network.RetrofitClient
import com.application.quickkartdeliverypartner.core.util.PreferencesManager
import com.application.quickkartdeliverypartner.data.mapper.AuthMapper
import com.application.quickkartdeliverypartner.data.remote.api.AuthApi
import com.application.quickkartdeliverypartner.data.repository.AuthRepositoryImpl
import com.application.quickkartdeliverypartner.domain.repository.AuthRepository
import com.application.quickkartdeliverypartner.domain.usecase.AuthUseCase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DeliveryLoginViewModel(
    private val context: Context
) : ViewModel() {

    private val preferencesManager = PreferencesManager(context)
    private val authApi: AuthApi = RetrofitClient.getClient().create(AuthApi::class.java)
    private val authMapper = AuthMapper()
    private val authRepository: AuthRepository = AuthRepositoryImpl(authApi, authMapper)
    private val authUseCase = AuthUseCase(authRepository)

    private fun sendFcmTokenToBackend() {
        viewModelScope.launch {
            var lastException: Exception? = null

            // Retry FCM token retrieval up to 3 times with exponential backoff
            for (attempt in 1..3) {
                try {
                    Log.d("FCM", "Delivery Partner FCM token retrieval attempt $attempt")
                    val fcmToken = FirebaseMessaging.getInstance().token.await()

                    // Initialize RetrofitClient with updated preferencesManager
                    RetrofitClient.init(preferencesManager)

                    // Create authenticated API client and make the call
                    val authApi = RetrofitClient.getAuthenticatedClient().create(AuthApi::class.java)

                    // Make direct API call
                    val response = authApi.updateFcmToken(mapOf("fcm_token" to fcmToken))
                    if (response.isSuccessful) {
                        Log.d("FCM", "FCM token sent to backend successfully after login")
                        return@launch // Success, exit function
                    } else {
                        Log.e("FCM", "Failed to send FCM token after login: ${response.message()}")
                        lastException = Exception("API call failed: ${response.message()}")
                    }
                } catch (e: Exception) {
                    Log.e("FCM", "Delivery Partner FCM token attempt $attempt failed: ${e.message}")
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
            Log.e("FCM", "All delivery partner FCM token retrieval attempts failed")
        }
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                val result = authUseCase.login(username, password)
                result.fold(
                    onSuccess = { authResponse ->
                        // Check if user is a delivery partner
                        if (authResponse.user.userType == "delivery_partner") {
                            // Save token and user data
                            preferencesManager.saveToken(authResponse.tokens.access)
                            preferencesManager.saveUser(authResponse.user)
                            preferencesManager.saveUserType(authResponse.user.userType)
                            _loginState.value = LoginState.Success
                            // Send FCM token to backend after successful login
                            sendFcmTokenToBackend()
                        } else {
                            _loginState.value = LoginState.Error("Access denied. This app is for delivery partners only.")
                        }
                    },
                    onFailure = { exception ->
                        _loginState.value = LoginState.Error("Login failed: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Login failed: ${e.message}")
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}