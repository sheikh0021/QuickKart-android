package com.application.quickkartcustomer.domain.usecase

import com.application.quickkartcustomer.domain.model.Product
import com.application.quickkartcustomer.domain.model.ProductListResponse
import com.application.quickkartcustomer.domain.repository.ProductRepository
import jakarta.inject.Inject


class ProductUseCase @Inject constructor(private val productRepository: ProductRepository) {
    suspend fun getProductsByStore(
        storeId: Int,
        page: Int = 1,
        search: String? = null
    ): Result<ProductListResponse> {
        return productRepository.getProductsByStore(storeId, page, search)
    }

    suspend fun getProductsByCategory(
        categoryId: Int,
        page: Int = 1,
        search: String? = null
    ): Result<ProductListResponse> {
        return productRepository.getProductsByCategory(categoryId, page, search)
    }

    suspend fun getProductDetails(productId: Int): Result<Product> {
        return productRepository.getProductDetails(productId)
    }
}