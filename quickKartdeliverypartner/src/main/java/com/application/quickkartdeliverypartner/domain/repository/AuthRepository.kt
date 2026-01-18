package com.application.quickkartdeliverypartner.domain.repository

import com.application.quickkartdeliverypartner.data.remote.dto.LoginRequest
import com.application.quickkartdeliverypartner.data.remote.dto.RegisterRequest
import com.application.quickkartdeliverypartner.domain.model.AuthResponse

interface AuthRepository {
    suspend fun login(request: LoginRequest): Result<AuthResponse>
    suspend fun register(request: RegisterRequest): Result<AuthResponse>
    suspend fun updateFcmToken(token: String): Result<String>
}