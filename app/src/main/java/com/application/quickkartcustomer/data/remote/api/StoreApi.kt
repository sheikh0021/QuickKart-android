package com.application.quickkartcustomer.data.remote.api

import com.application.quickkartcustomer.data.remote.dto.StoreDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface StoreApi {
@GET("stores/")
suspend fun getNearbyStores() : Response<List<StoreDto>>

@GET("stores/{id}")
suspend fun getStoreDetails(@Path("id") storeId: Int) : Response<StoreDto>
}