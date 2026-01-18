package com.application.quickkartadmin.domain.model


data class Admin(
    val token: String,
    val user : AdminUser
)

data class AdminUser(
    val id: Int,
    val username: String,
    val fullName: String,
    val userType: String
)

