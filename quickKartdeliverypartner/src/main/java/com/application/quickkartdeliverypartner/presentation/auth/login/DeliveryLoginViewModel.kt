package com.application.quickkartdeliverypartner.presentation.auth.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartdeliverypartner.core.network.RetrofitClient
import com.application.quickkartdeliverypartner.core.util.PreferencesManager
import com.application.quickkartdeliverypartner.data.mapper.AuthMapper
import com.application.quickkartdeliverypartner.data.remote.api.AuthApi
import com.application.quickkartdeliverypartner.data.repository.AuthRepositoryImpl
import com.application.quickkartdeliverypartner.domain.repository.AuthRepository
import com.application.quickkartdeliverypartner.domain.usecase.AuthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeliveryLoginViewModel(
    private val context: Context
) : ViewModel() {

    private val preferencesManager = PreferencesManager(context)
    private val authApi: AuthApi = RetrofitClient.getClient().create(AuthApi::class.java)
    private val authMapper = AuthMapper()
    private val authRepository: AuthRepository = AuthRepositoryImpl(authApi, authMapper)
    private val authUseCase = AuthUseCase(authRepository)

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