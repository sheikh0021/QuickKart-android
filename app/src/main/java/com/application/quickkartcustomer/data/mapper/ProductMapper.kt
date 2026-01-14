package com.application.quickkartcustomer.data.mapper

import com.application.quickkartcustomer.data.remote.dto.ProductDto
import com.application.quickkartcustomer.domain.model.Product


class ProductMapper {
    fun mapToDomain(dto: ProductDto): Product {
        return Product(
            id = dto.id,
            store = dto.storeId,
            category = dto.categoryId,
            name = dto.name,
            description = dto.description,
            price = dto.price,
            image = dto.image,
            unit = dto.unit,
            stockQuantity = dto.stockQuantity,
            isAvailable = dto.isAvailable,
            categoryName = dto.categoryName,
            storeName = dto.storeName
        )
    }
}

// Extension function to convert list of ProductDto to list of Product
fun List<ProductDto>.toProductList(): List<Product> {
    val mapper = ProductMapper()
    return this.map { mapper.mapToDomain(it) }
}