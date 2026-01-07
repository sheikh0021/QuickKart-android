package com.application.quickkartcustomer.data.remote.dto

import com.google.gson.annotations.SerializedName


data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("user_type") val userType : String,
    @SerializedName("profile_image") val profileImage: String? = null
)

data class UpdateProfileDto(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    val email: String
)

data class ProfileDto(
    val id: Int,
    val user: UserDto,
    val address: String,
    val latitude: Double?,
    val longitude: Double?,
    @SerializedName("preferred_payment") val preferredPayment: String
)