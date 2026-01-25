package com.application.quickkartcustomer.data.mapper

import com.application.quickkartcustomer.data.remote.dto.CategoryDto
import com.application.quickkartcustomer.domain.model.Category

// Default category images (you'll replace these with actual Figma assets)
private fun getDefaultCategoryImages(categoryName: String): List<String> {
    val baseName = categoryName.lowercase().replace(" ", "_").replace("&", "and")
    return listOf(
        "categories/${baseName}/${baseName}_01.png",
        "categories/${baseName}/${baseName}_02.png",
        "categories/${baseName}/${baseName}_03.png"
    )
}

fun CategoryDto.toCategory(): Category {
    val defaultImages = getDefaultCategoryImages(this.name)
    return Category(
        id = this.id,
        name = this.name,
        image = this.image,
        images = if (this.images?.isNotEmpty() == true) this.images else defaultImages,
        isActive = this.is_active
    )
}

fun List<CategoryDto>.toCategoryList(): List<Category> {
    return this.map { it.toCategory() }
}