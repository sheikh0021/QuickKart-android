package com.application.quickkartcustomer.data.mapper

import com.application.quickkartcustomer.data.remote.dto.StoreDto
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
}