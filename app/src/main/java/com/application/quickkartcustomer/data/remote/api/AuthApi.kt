package com.application.quickkartcustomer.data.remote.api

import com.application.quickkartcustomer.data.remote.dto.AuthResponseDto
import com.application.quickkartcustomer.data.remote.dto.LoginRequest
import com.application.quickkartcustomer.data.remote.dto.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi{
    @POST("users/login/")
    suspend fun login(@Body request: LoginRequest) : Response<AuthResponseDto>

    @POST("users/register/")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponseDto>
}