package com.application.quickkartcustomer.domain.usecase

import com.application.quickkartcustomer.domain.model.UpdateProfileRequest
import com.application.quickkartcustomer.domain.model.UserProfile
import com.application.quickkartcustomer.domain.repository.ProfileRepository
import javax.inject.Inject


class ProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend fun getProfile(): Result<UserProfile> {
        return profileRepository.getProfile()
    }

    suspend fun updateProfile(request: UpdateProfileRequest): Result<UserProfile> {
        if (request.firstName.isBlank()) {
            return Result.failure(Exception("First name cannot be empty"))
        }
        if (request.phoneNumber.isBlank()) {
            return Result.failure(Exception("Phone number cannot be empty"))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(request.email).matches()) {
            return Result.failure(Exception("Invalid email address"))
        }
        return profileRepository.updateProfile(request)
    }
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<String> {
        if (oldPassword.isBlank() || newPassword.isBlank()) {
            return Result.failure(Exception("Passwords cannot be empty"))
        }
        if (newPassword.length < 6) {
            return Result.failure(Exception("New Password must be at least 6 characters"))
        }
        return profileRepository.changePassword(oldPassword, newPassword)
    }

    suspend fun logout(): Result<String> {
        return profileRepository.logout()
    }

}