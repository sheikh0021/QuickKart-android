package com.application.quickkartcustomer.data.remote.dto

data class CategoryDto(
    val id: Int,
    val name: String,
    val image: String?,
    val images: List<String>? = null, // Multiple images for sliding animation - can be null from API
    val is_active: Boolean
)