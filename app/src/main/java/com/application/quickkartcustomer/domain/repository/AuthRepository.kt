package com.application.quickkartcustomer.domain.repository

import com.application.quickkartcustomer.data.remote.dto.LoginRequest
import com.application.quickkartcustomer.data.remote.dto.RegisterRequest
import com.application.quickkartcustomer.domain.model.AuthResponse

interface AuthRepository {
    suspend fun login(request: LoginRequest): Result<AuthResponse>
    suspend fun register(request: RegisterRequest): Result<AuthResponse>
}