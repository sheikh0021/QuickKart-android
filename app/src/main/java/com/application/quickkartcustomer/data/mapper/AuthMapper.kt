package com.application.quickkartcustomer.data.mapper

import com.application.quickkartcustomer.data.remote.dto.AuthResponseDto
import com.application.quickkartcustomer.domain.model.AuthResponse
import com.application.quickkartcustomer.domain.model.AuthTokens
import com.application.quickkartcustomer.domain.model.User

class AuthMapper {
    fun mapToDomain(dto: AuthResponseDto): AuthResponse{
        return AuthResponse(
            user = User(
                id = dto.user.id,
                username = dto.user.username,
                email = dto.user.email,
                firstName = dto.user.firstName,
                lastName = dto.user.lastName,
                userType = dto.user.userType,
                phoneNumber = dto.user.phoneNumber
            ),
            tokens = AuthTokens(
                access = dto.tokens.access,
                refresh = dto.tokens.refresh
            )
        )
    }
}