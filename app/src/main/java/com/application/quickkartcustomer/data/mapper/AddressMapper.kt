package com.application.quickkartcustomer.data.mapper

import com.application.quickkartcustomer.data.remote.dto.AddressDto
import com.application.quickkartcustomer.domain.model.Address


fun AddressDto.toDomain(): Address {
    return Address(
        id = id,
        street = street,
        city = city,
        state = state,
        zipCode = zipCode,
        latitude = latitude,
        longitude = longitude,
        isDefault = isDefault
    )
}

fun Address.toDto(): AddressDto{
    return AddressDto(
        id = id,
        street = street,
        city = city,
        state = state,
        zipCode = zipCode,
        latitude = latitude,
        longitude = longitude,
        isDefault = isDefault
    )
}