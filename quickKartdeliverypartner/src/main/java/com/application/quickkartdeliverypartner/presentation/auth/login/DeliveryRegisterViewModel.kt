package com.application.quickkartdeliverypartner.presentation.auth.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartdeliverypartner.core.network.RetrofitClient
import com.application.quickkartdeliverypartner.data.mapper.AuthMapper
import com.application.quickkartdeliverypartner.data.remote.api.AuthApi
import com.application.quickkartdeliverypartner.data.repository.AuthRepositoryImpl
import com.application.quickkartdeliverypartner.domain.repository.AuthRepository
import com.application.quickkartdeliverypartner.domain.usecase.AuthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeliveryRegisterViewModel(
    private val context: Context
) : ViewModel() {

    private val authApi: AuthApi = RetrofitClient.getClient().create(AuthApi::class.java)
    private val authMapper = AuthMapper()
    private val authRepository: AuthRepository = AuthRepositoryImpl(authApi, authMapper)
    private val authUseCase = AuthUseCase(authRepository)

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun register(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String
    ) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            try {
                val result = authUseCase.register(
                    username = username,
                    email = email,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber
                )

                result.fold(
                    onSuccess = { authResponse ->
                        _registerState.value = RegisterState.Success
                    },
                    onFailure = { exception ->
                        _registerState.value = RegisterState.Error("Registration failed: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Registration failed: ${e.message}")
            }
        }
    }
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}