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
                firstName = dto.user.first_name,
                lastName = dto.user.last_name,
                userType = dto.user.user_type,
                phoneNumber = dto.user.phone_number
            ),
            tokens = AuthTokens(
                access = dto.tokens.access,
                refresh = dto.tokens.refresh
            )
        )
    }
}