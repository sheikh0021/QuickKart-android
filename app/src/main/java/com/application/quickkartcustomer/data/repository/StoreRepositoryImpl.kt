package com.application.quickkartcustomer.data.repository

import com.application.quickkartcustomer.data.mapper.StoreMapper
import com.application.quickkartcustomer.data.mapper.toCategoryList
import com.application.quickkartcustomer.data.mapper.toProductList
import com.application.quickkartcustomer.data.remote.api.StoreApi
import com.application.quickkartcustomer.domain.model.Banner
import com.application.quickkartcustomer.domain.model.Category
import com.application.quickkartcustomer.domain.model.HomeData
import com.application.quickkartcustomer.domain.model.Product
import com.application.quickkartcustomer.domain.model.Store
import com.application.quickkartcustomer.domain.repository.StoreRepository

class StoreRepositoryImpl(
    private val storeApi: StoreApi,
    private val storeMapper: StoreMapper
) : StoreRepository {
    override suspend fun getNearbyStores(): Result<List<Store>> {
        return try {
            val response = storeApi.getNearbyStores()
            if (response.isSuccessful) {
                response.body()?.let { dtoList ->
                    Result.success(dtoList.map { storeMapper.mapToDomain(it) })
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to fetch stores: ${response.message()}"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getStoreDetails(storeId: Int): Result<Store> {
        return try {
            val response = storeApi.getStoreDetails(storeId)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(storeMapper.mapToDomain(dto))
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to fetch store details: ${response.message()}"))
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getHomeData(): Result<HomeData> {
        return try {
            val response = storeApi.getHomeData()
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    val stores = dto.stores.map { storeMapper.mapToDomain(it) }
                    val categories = dto.categories.toCategoryList()
                    val banners = dto.banners.map { Banner(it.id, it.image, it.title, it.description) }

                    Result.success(HomeData(stores, categories, banners))
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to load home data: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductsByCategory(categoryId: Int): Result<List<Product>> {
        return try {
            val response = storeApi.getProductsByCategory(categoryId)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.products.toProductList())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to load products: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}