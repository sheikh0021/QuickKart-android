package com.application.quickkartcustomer.domain.repository

import com.application.quickkartcustomer.domain.model.Product
import com.application.quickkartcustomer.domain.model.ProductListResponse

interface ProductRepository {
    suspend fun getProductsByStore(
        storeId: Int,
        page: Int = 1,
        search: String? = null
    ): Result<ProductListResponse>

    suspend fun getProductsByCategory(
        categoryId: Int,
        page: Int = 1,
        search: String? = null
    ): Result<ProductListResponse>

    suspend fun getProductDetails(productId: Int): Result<Product>
}