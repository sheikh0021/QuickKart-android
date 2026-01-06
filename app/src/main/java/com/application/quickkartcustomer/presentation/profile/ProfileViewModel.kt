package com.application.quickkartcustomer.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartcustomer.core.util.PreferencesManager
import com.application.quickkartcustomer.domain.model.UpdateProfileRequest
import com.application.quickkartcustomer.domain.model.UserProfile
import com.application.quickkartcustomer.domain.usecase.ProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUseCase: ProfileUseCase,
    private val preferencesManager: PreferencesManager
): ViewModel() {
    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut: StateFlow<Boolean> = _isLoggedOut

    init {
        loadProfile()
    }

    fun loadProfile(){
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            profileUseCase.getProfile().fold(
                onSuccess = {profile ->
                    _profile.value = profile
                    _isLoading.value = false
                },
                onFailure = {exception ->
                    _error.value = exception.message ?: "Failed to load profile"
                    _isLoading.value = false
                }
            )
        }
    }
    fun updateProfile(firstName: String, lastName: String, phoneNumber: String, email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null

            val request = UpdateProfileRequest(
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                email = email
            )

            profileUseCase.updateProfile(request).fold(
                onSuccess = { profile ->
                    _profile.value = profile
                    _successMessage.value = "Profile updated successfully"
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Failed to update profile"
                    _isLoading.value = false
                }
            )
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null

            profileUseCase.changePassword(oldPassword, newPassword).fold(
                onSuccess = { message ->
                    _successMessage.value = message
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Failed to change password"
                    _isLoading.value = false
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true

            profileUseCase.logout().fold(
                onSuccess = {
                    preferencesManager.clearData()
                    _isLoggedOut.value = true
                    _isLoading.value = false
                },
                onFailure = {
                    // Even if API fails, clear local data
                    preferencesManager.clearData()
                    _isLoggedOut.value = true
                    _isLoading.value = false
                }
            )
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}