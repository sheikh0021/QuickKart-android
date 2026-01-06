package com.application.quickkartcustomer.domain.model

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val userType: String,
    val phoneNumber: String
)

data class AuthTokens(
    val access: String,
    val refresh: String
)

data class AuthResponse(
    val user: User,
    val tokens: AuthTokens
)

data class UserProfile(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val profileImage: String? = null,
    val defaultAddress: Address? = null
)

data class UpdateProfileRequest(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String
)