package com.application.quickkartcustomer.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("user_type") val userType: String = "customer"
)

data class AuthResponseDto(
    val user: UserDto,
    val tokens: TokensDto
)

data class TokensDto(
    val access: String,
    val refresh: String
)