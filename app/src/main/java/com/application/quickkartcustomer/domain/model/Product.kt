package com.application.quickkartcustomer.domain.model



data class Category(
    val id: Int,
    val name: String,
    val image: String?,
    val isActive: Boolean
)

data class Product(
    val id: Int,
    val store: Int,
    val category: Int,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val unit: String,
    val stockQuantity: Int,
    val isAvailable: Boolean,
    val categoryName: String,
    val storeName: String
)

data class ProductListResponse(
    val products: List<Product>,
    val totalPages: Int,
    val currentPage: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)
