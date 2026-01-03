package com.application.quickkartcustomer.data.repository

import com.application.quickkartcustomer.data.mapper.ProductMapper
import com.application.quickkartcustomer.data.remote.api.StoreApi
import com.application.quickkartcustomer.domain.model.Product
import com.application.quickkartcustomer.domain.model.ProductListResponse
import com.application.quickkartcustomer.domain.repository.ProductRepository


class ProductRepositoryImpl(
    private val storeApi: StoreApi,
    private val productMapper: ProductMapper
) : ProductRepository {
    override suspend fun getProductsByStore(
        storeId: Int,
        page: Int,
        search: String?
    ): Result<ProductListResponse> {
        return try {
            val response = storeApi.getProductsByStore(storeId, page, search)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    val products = dto.products.map { productMapper.mapToDomain(it) }
                    val result = ProductListResponse(
                        products = products,
                        totalPages = dto.totalPages,
                        currentPage = dto.currentPage,
                        hasNext = dto.hasNext,
                        hasPrevious = dto.hasPrevious
                    )
                    Result.success(result)
                } ?: Result.failure(Exception("Empty Response"))
            } else {
                Result.failure(Exception("Failed to load products: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductsByCategory(
        categoryId: Int,
        page: Int,
        search: String?
    ): Result<ProductListResponse> {
        return try {
            val response = storeApi.getProductsByCategory(categoryId, page, search)
            if (response.isSuccessful){
                response.body()?.let { dto ->
                    val products = dto.products.map { productMapper.mapToDomain(it) }
                    val result = ProductListResponse(
                        products = products,
                        totalPages = dto.totalPages,
                        currentPage = dto.currentPage,
                        hasNext = dto.hasNext,
                        hasPrevious = dto.hasPrevious
                    )
                    Result.success(result)
                } ?: Result.failure(Exception("Empty Response"))
            } else {
                Result.failure(Exception("Failed to load products: ${response.message()}"))
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getProductDetails(productId: Int): Result<Product> {
        //just for now we are returning failure because a separate endpoint is needed.
        return Result.failure(Exception("Product details not implemented yet"))
    }
}