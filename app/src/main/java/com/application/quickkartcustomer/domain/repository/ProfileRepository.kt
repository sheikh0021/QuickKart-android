package com.application.quickkartcustomer.domain.repository

import com.application.quickkartcustomer.domain.model.UpdateProfileRequest
import com.application.quickkartcustomer.domain.model.UserProfile


interface ProfileRepository {
    suspend fun getProfile(): Result<UserProfile>
    suspend fun updateProfile(request: UpdateProfileRequest): Result<UserProfile>
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<String>
    suspend fun logout(): Result<String>
    suspend fun updateFcmToken(token: String): Result<String>
}