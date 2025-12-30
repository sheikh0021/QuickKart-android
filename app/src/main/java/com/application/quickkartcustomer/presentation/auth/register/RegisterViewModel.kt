package com.application.quickkartcustomer.presentation.auth.register

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartcustomer.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
): ViewModel() {
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun register(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String
    ){
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            authUseCase.register(username, email, password, firstName, lastName, phoneNumber).fold(
                onSuccess = {authResponse ->
                    _registerState.value = RegisterState.Success
                },
                onFailure = {exception ->
                    _registerState.value = RegisterState.Error(exception.message ?: "Registration failed")
                }
            )
        }
    }
}

sealed class RegisterState{
    object Idle: RegisterState()
    object Loading: RegisterState()
    object Success: RegisterState()
    data class Error(val message: String): RegisterState()
}