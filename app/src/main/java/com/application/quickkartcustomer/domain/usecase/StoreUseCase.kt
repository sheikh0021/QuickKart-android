package com.application.quickkartcustomer.domain.usecase

import com.application.quickkartcustomer.domain.model.HomeData
import com.application.quickkartcustomer.domain.model.Store
import com.application.quickkartcustomer.domain.repository.StoreRepository

class StoreUseCase(private val storeRepository: StoreRepository) {
    suspend fun getNearbyStores(): Result<List<Store>> {
        return storeRepository.getNearbyStores()
    }
    suspend fun getStoreDetails(storeId: Int): Result<Store> {
        return storeRepository.getStoreDetails(storeId)
    }
    suspend fun getHomeData(): Result<HomeData>{
        return storeRepository.getHomeData()
    }
}