package com.application.quickkartcustomer.domain.model



data class Category(
    val id: Int,
    val name: String,
    val image: String?, // Keep for backward compatibility
    val images: List<String> = emptyList(), // New: multiple images for sliding animation
    val isActive: Boolean
)

data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val image: String?,
    val store: Int,
    val storeName: String? = null, // Add this field for carousel display
    val category: Int? = null,
    val quantity: Int = 1,
    val isAvailable: Boolean = true
)

data class ProductListResponse(
    val products: List<Product>,
    val totalPages: Int,
    val currentPage: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)
