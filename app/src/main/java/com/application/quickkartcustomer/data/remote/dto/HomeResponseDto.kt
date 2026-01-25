package com.application.quickkartcustomer.data.remote.dto

data class HomeResponseDto(
    val stores: List<StoreDto>? = null,
    val categories: List<CategoryDto>? = null,
    val banners: List<BannerDto>? = null,
    val products: List<ProductDto>? = null // ADD THIS LINE
)