package com.application.quickkartcustomer.data.remote.dto



data class StoreDto(
    val id: Int,
    val name: String,
    val description: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone_number: String,
    val image: String?,
    val is_Active: Boolean,
    val delivery_radius: Int,
    val minimum_order: Double,
    val delivery_fee: Double
)


data class HomeResponseDto(
    val stores: List<StoreDto>,
    val categories : List<CategoryDto>,
    val banners: List<BannerDto>
)