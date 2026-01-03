package com.application.quickkartcustomer.data.remote.dto

import com.google.gson.annotations.SerializedName


data class ProductDto(
val id: Int,
    @SerializedName("store") val storeId: Int,
    @SerializedName("category") val categoryId: Int,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val unit: String,
    @SerializedName("stock_quantity") val stockQuantity: Int,
    @SerializedName("is_available") val isAvailable: Boolean,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("store_name") val storeName: String
)

data class ProductListResponseDto(
    val products: List<ProductDto>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("current_page") val currentPage: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)