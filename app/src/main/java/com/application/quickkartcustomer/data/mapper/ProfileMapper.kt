package com.application.quickkartcustomer.data.mapper

import com.application.quickkartcustomer.data.remote.dto.UpdateProfileDto
import com.application.quickkartcustomer.data.remote.dto.UserDto
import com.application.quickkartcustomer.domain.model.UpdateProfileRequest
import com.application.quickkartcustomer.domain.model.User
import com.application.quickkartcustomer.domain.model.UserProfile
import javax.inject.Inject


class ProfileMapper @Inject constructor() {
    fun mapUserDtoToDomain(dto: UserDto): User {
        return User(
            id = dto.id,
            username = dto.username,
            email = dto.email,
            firstName = dto.firstName,
            lastName = dto.lastName,
            userType = dto.userType,
            phoneNumber = dto.phoneNumber
        )
    }

    fun mapUserDtoToProfile(dto: UserDto): UserProfile{
        return UserProfile(
            id = dto.id,
            username = dto.username,
            email = dto.email,
            firstName = dto.firstName,
            lastName = dto.lastName,
            phoneNumber = dto.phoneNumber,
            profileImage = dto.profileImage
        )
    }

    fun mapUpdateRequestToDto(request: UpdateProfileRequest): UpdateProfileDto{
        return UpdateProfileDto(
            firstName = request.firstName,
            lastName = request.lastName,
            phoneNumber = request.phoneNumber,
            email = request.email
        )
    }

}