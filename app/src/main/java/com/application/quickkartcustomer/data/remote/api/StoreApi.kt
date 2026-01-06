package com.application.quickkartcustomer.data.remote.api

import com.application.quickkartcustomer.data.remote.dto.HomeResponseDto
import com.application.quickkartcustomer.data.remote.dto.ProductListResponseDto
import com.application.quickkartcustomer.data.remote.dto.StoreDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface StoreApi {
@GET("stores/")
suspend fun getNearbyStores() : Response<List<StoreDto>>

@GET("stores/{id}")
suspend fun getStoreDetails(@Path("id") storeId: Int) : Response<StoreDto>

@GET("stores/home/")
suspend fun getHomeData(): Response<HomeResponseDto>

@GET("products/")
suspend fun getProductsByStore(
    @Query("store") storeId: Int,
    @Query("page") page: Int = 1,
    @Query("search") search: String? = null
): Response<ProductListResponseDto>

@GET("products/")
suspend fun getProductsByCategory(
    @Query("category") categoryId: Int,
    @Query("page") page: Int = 1,
    @Query("search") search: String? = null
): Response<ProductListResponseDto>
}