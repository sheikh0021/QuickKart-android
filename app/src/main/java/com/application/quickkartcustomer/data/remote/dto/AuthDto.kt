package com.application.quickkartcustomer.data.remote.dto

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phone_number: String,
    val userType: String = "customer"
)

data class AuthResponseDto(
    val user: UserDto,
    val tokens: TokensDto
)

data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val user_type: String,
    val phone_number: String
)

data class TokensDto(
    val access: String,
    val refresh: String
)