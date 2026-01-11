package com.application.quickkartdeliverypartner.data.repository

import com.application.quickkartdeliverypartner.data.mapper.AuthMapper
import com.application.quickkartdeliverypartner.data.remote.api.AuthApi
import com.application.quickkartdeliverypartner.data.remote.dto.LoginRequest
import com.application.quickkartdeliverypartner.data.remote.dto.RegisterRequest
import com.application.quickkartdeliverypartner.domain.model.AuthResponse
import com.application.quickkartdeliverypartner.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val authMapper: AuthMapper
) : AuthRepository {

    override suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = authApi.login(request)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(authMapper.mapToDomain(dto))
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val response = authApi.register(request)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(authMapper.mapToDomain(dto))
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}