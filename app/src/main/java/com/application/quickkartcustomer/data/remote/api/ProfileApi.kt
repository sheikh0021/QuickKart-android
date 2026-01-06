package com.application.quickkartcustomer.data.remote.api

import com.application.quickkartcustomer.data.remote.dto.UpdateProfileDto
import com.application.quickkartcustomer.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT


interface ProfileApi {
    @GET("api/users/profile/")
    suspend fun getProfile(): Response<UserDto>

    @PUT("api/users/profile/")
    suspend fun updateProfile(@Body request: UpdateProfileDto): Response<UserDto>

    @POST("api/users/change-password/")
    suspend fun changePassword(
        @Body request: Map<String, String>
    ): Response<Map<String, String>>

    @POST("api/auth/logout/")
    suspend fun logout(): Response<Map<String, String>>
}