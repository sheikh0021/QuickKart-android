package com.application.quickkartcustomer.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartcustomer.domain.model.Category
import com.application.quickkartcustomer.domain.model.Product
import com.application.quickkartcustomer.domain.usecase.StoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val storeUseCase: StoreUseCase
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _categoryProducts = MutableStateFlow<Map<Int, List<Product>>>(emptyMap())
    val categoryProducts: StateFlow<Map<Int, List<Product>>> = _categoryProducts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            storeUseCase.getHomeData().fold(
                onSuccess = { homeData ->
                    _categories.value = homeData.categories
                    // Load products for each category
                    loadProductsForCategories(homeData.categories)
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Failed to load categories"
                    _isLoading.value = false
                }
            )
        }
    }

    private fun loadProductsForCategories(categories: List<Category>) {
        viewModelScope.launch {
            val productsMap = mutableMapOf<Int, List<Product>>()

            categories.forEach { category ->
                storeUseCase.getProductsByCategory(category.id).fold(
                    onSuccess = { products ->
                        productsMap[category.id] = products
                    },
                    onFailure = { exception ->
                        // Handle individual category failure silently
                        productsMap[category.id] = emptyList()
                    }
                )
            }

            _categoryProducts.value = productsMap
        }
    }

    fun loadProductsForCategory(categoryId: Int) {
        viewModelScope.launch {
            storeUseCase.getProductsByCategory(categoryId).fold(
                onSuccess = { products ->
                    val currentMap = _categoryProducts.value.toMutableMap()
                    currentMap[categoryId] = products
                    _categoryProducts.value = currentMap
                },
                onFailure = { exception ->
                    // Could emit error for specific category
                }
            )
        }
    }
}