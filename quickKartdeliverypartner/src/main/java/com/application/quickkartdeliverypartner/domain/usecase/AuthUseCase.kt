package com.application.quickkartdeliverypartner.domain.usecase

import com.application.quickkartdeliverypartner.data.remote.dto.LoginRequest
import com.application.quickkartdeliverypartner.data.remote.dto.RegisterRequest
import com.application.quickkartdeliverypartner.domain.model.AuthResponse
import com.application.quickkartdeliverypartner.domain.repository.AuthRepository
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend fun login(username: String, password: String): Result<AuthResponse> {
        val request = LoginRequest(username, password)
        return authRepository.login(request)
    }

    suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): Result<AuthResponse> {
        val request = RegisterRequest(
            username = username,
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            userType = "delivery_partner"
        )
        return authRepository.register(request)
    }

    suspend fun updateFcmToken(token: String): Result<String> {
        return authRepository.updateFcmToken(token)
    }
}