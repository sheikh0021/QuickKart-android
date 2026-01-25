package com.application.quickkartcustomer.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.application.quickkartcustomer.domain.model.Product


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
) {
    fun toDomain(): Product {
        return Product(
            id = id,
            name = name,
            description = description,
            price = price,
            image = image,
            store = storeId,
            storeName = storeName,
            category = categoryId,
            quantity = 1,
            isAvailable = isAvailable
        )
    }
}

data class ProductListResponseDto(
    @SerializedName("results") val products: List<ProductDto>,
    val count: Int,
    val next: String?,
    val previous: String?,
) {
    val totalPages: Int
        get() = if (count > 0) (count + 19) / 20 else 0

    val currentPage: Int
        get() = 1

    val hasNext: Boolean
        get() = next != null

    val hasPrevious: Boolean
        get() = previous != null
}