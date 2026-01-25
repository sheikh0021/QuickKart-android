package com.application.quickkartcustomer.data.mapper

import com.application.quickkartcustomer.data.remote.dto.ProductDto
import com.application.quickkartcustomer.domain.model.Product


class ProductMapper {
    fun mapToDomain(dto: ProductDto): Product {
        return Product(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            price = dto.price,
            image = dto.image,
            store = dto.storeId,
            storeName = dto.storeName,
            category = dto.categoryId,
            quantity = 1,
            isAvailable = dto.isAvailable
        )
    }
}

// Extension function to convert list of ProductDto to list of Product
fun List<ProductDto>.toProductList(): List<Product> {
    val mapper = ProductMapper()
    return this.map { mapper.mapToDomain(it) }
}