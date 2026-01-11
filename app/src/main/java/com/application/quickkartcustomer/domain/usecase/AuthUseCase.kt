package com.application.quickkartcustomer.domain.usecase

import com.application.quickkartcustomer.data.remote.dto.LoginRequest
import com.application.quickkartcustomer.data.remote.dto.RegisterRequest
import com.application.quickkartcustomer.domain.model.AuthResponse
import com.application.quickkartcustomer.domain.repository.AuthRepository

class AuthUseCase(private val authRepository: AuthRepository) {
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
        phoneNumber: String,
        userType: String
    ) : Result<AuthResponse>{
        val request = RegisterRequest(
            username = username,
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            userType = userType
        )
        return authRepository.register(request)
    }
}