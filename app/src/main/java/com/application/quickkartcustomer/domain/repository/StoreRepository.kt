package com.application.quickkartcustomer.domain.repository

import com.application.quickkartcustomer.domain.model.Store

interface StoreRepository {
    suspend fun getNearbyStores(): Result<List<Store>>
    suspend fun getStoreDetails(storeId: Int): Result<Store>
}