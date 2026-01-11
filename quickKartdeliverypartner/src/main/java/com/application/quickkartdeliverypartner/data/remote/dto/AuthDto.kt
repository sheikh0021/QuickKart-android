package com.application.quickkartdeliverypartner.data.remote.dto

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
    @SerializedName("user_type") val userType: String = "delivery_partner"
)

data class AuthResponseDto(
    val user: UserDto,
    val tokens: TokensDto
)

data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("user_type") val userType: String,
    @SerializedName("profile_image") val profileImage: String? = null
)

data class TokensDto(
    val access: String,
    val refresh: String
)