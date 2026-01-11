package com.application.quickkartcustomer.data.mapper


import com.application.quickkartcustomer.data.mapper.toCategoryList
import com.application.quickkartcustomer.data.remote.dto.BannerDto
import com.application.quickkartcustomer.data.remote.dto.CategoryDto
import com.application.quickkartcustomer.data.remote.dto.StoreDto
import com.application.quickkartcustomer.domain.model.Banner
import com.application.quickkartcustomer.domain.model.Category
import com.application.quickkartcustomer.domain.model.Store

class StoreMapper {
    fun mapToDomain(dto: StoreDto) : Store {
        return Store(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            address = dto.address,
            latitude = dto.latitude,
            longitude = dto.longitude,
            phoneNumber = dto.phone_number,
            image = dto.image,
            isActive = dto.is_Active,
            deliveryRadius = dto.delivery_radius,
            minimumOrder = dto.minimum_order,
            deliveryFee = dto.delivery_fee
        )
    }
    fun mapCategoryToDomain(dto: CategoryDto): Category{
        return Category(
            id = dto.id,
            name = dto.name,
            image = dto.image,
            isActive = dto.is_active
        )
    }

    fun mapBannerToDomain(dto: BannerDto): Banner{
        return Banner(
            id= dto.id,
            image = dto.image,
            title = dto.image,
            description = dto.description
        )
    }
}