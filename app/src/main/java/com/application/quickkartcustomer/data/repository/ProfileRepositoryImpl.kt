package com.application.quickkartcustomer.data.repository

import com.application.quickkartcustomer.data.mapper.ProfileMapper
import com.application.quickkartcustomer.data.remote.api.ProfileApi
import com.application.quickkartcustomer.domain.model.UpdateProfileRequest
import com.application.quickkartcustomer.domain.model.UserProfile
import com.application.quickkartcustomer.domain.repository.ProfileRepository
import javax.inject.Inject


class ProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi,
    private val profileMapper: ProfileMapper
): ProfileRepository {
    override suspend fun getProfile(): Result<UserProfile> {
        return try {
            val response = profileApi.getProfile()
            if (response.isSuccessful && response.body() != null) {
                val profile = profileMapper.mapUserDtoToProfile(response.body()!!)
                Result.success(profile)
            } else {
                Result.failure(Exception("Failed to load profile: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): Result<UserProfile> {
        return try {
            val dto = profileMapper.mapUpdateRequestToDto(request)
            val response = profileApi.updateProfile(dto)
            if (response.isSuccessful && response.body() != null) {
                val profile = profileMapper.mapUserDtoToProfile(response.body()!!)
                Result.success(profile)
            } else {
                Result.failure(Exception("Failed to update profile: ${response.message()}"))
            }
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<String> {
        return try {
            val request = mapOf(
                "old_password" to oldPassword,
                "new_password" to newPassword
            )
            val response = profileApi.changePassword(request)
            if (response.isSuccessful) {
                Result.success("Password changed successfully.")
            } else {
                Result.failure(Exception("Failed to change password: ${response.message()}"))
            }
        } catch (e: Exception ) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<String> {
        return try {
            val response = profileApi.logout()
            if (response.isSuccessful) {
                Result.success("Logged out successfully")
            } else {
                Result.failure(Exception("Logout failed: ${response.message()}"))
            }
        } catch ( e: Exception) {
            Result.failure(e)
        }
    }
}