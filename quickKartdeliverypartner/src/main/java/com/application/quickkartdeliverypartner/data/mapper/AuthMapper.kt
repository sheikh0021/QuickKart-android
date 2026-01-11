package com.application.quickkartdeliverypartner.data.mapper

import com.application.quickkartdeliverypartner.data.remote.dto.AuthResponseDto
import com.application.quickkartdeliverypartner.data.remote.dto.UserDto
import com.application.quickkartdeliverypartner.domain.model.AuthResponse
import com.application.quickkartdeliverypartner.domain.model.Tokens
import com.application.quickkartdeliverypartner.domain.model.User

class AuthMapper {

    fun mapToDomain(dto: AuthResponseDto): AuthResponse {
        return AuthResponse(
            user = mapUserToDomain(dto.user),
            tokens = Tokens(
                access = dto.tokens.access,
                refresh = dto.tokens.refresh
            )
        )
    }

    private fun mapUserToDomain(dto: UserDto): User {
        return User(
            id = dto.id,
            username = dto.username,
            email = dto.email,
            firstName = dto.firstName,
            lastName = dto.lastName,
            phoneNumber = dto.phoneNumber,
            userType = dto.userType,
            profileImage = dto.profileImage
        )
    }
}