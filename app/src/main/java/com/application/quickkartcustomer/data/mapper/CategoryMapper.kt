package com.application.quickkartcustomer.data.mapper

import com.application.quickkartcustomer.data.remote.dto.CategoryDto
import com.application.quickkartcustomer.domain.model.Category


fun CategoryDto.toCategory(): Category {
     return Category(
         id = this.id,
         name = this.name,
         image = this.image,
         isActive = this.is_active
     )
}

fun List<CategoryDto>.toCategoryList(): List<Category> {
    return this.map { it.toCategory() }
}