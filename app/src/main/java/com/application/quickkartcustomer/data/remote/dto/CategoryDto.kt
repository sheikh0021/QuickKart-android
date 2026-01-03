package com.application.quickkartcustomer.data.remote.dto

data class CategoryDto(
    val id: Int,
    val name: String,
    val image: String?,
    val is_active: Boolean
)