package com.application.quickkartcustomer.presentation.product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartcustomer.domain.model.Product
import com.application.quickkartcustomer.domain.usecase.CartUseCase
import com.application.quickkartcustomer.domain.usecase.ProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productUseCase: ProductUseCase,
    private val cartUseCase: CartUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    private val _hasNextPage = MutableStateFlow(false)
    val hasNextPage: StateFlow<Boolean> = _hasNextPage

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private var storeId: Int? = null
    private var categoryId: Int? = null

    private val _addToCartSuccess = MutableStateFlow<String?>(null)
    val addToCartSuccess: StateFlow<String?> = _addToCartSuccess

    private val _cartError = MutableStateFlow<String?>(null)
    val cartError: StateFlow<String?> = _cartError

    init {
        //get parameters from navigation arguments
        storeId = savedStateHandle.get<Int>("storeId")
        categoryId = savedStateHandle.get<Int>("categoryId")

        loadProducts()
    }

    fun loadProducts(page: Int = 1, search: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = when {
                storeId != null -> productUseCase.getProductsByStore(storeId!!, page, search)
                categoryId != null -> productUseCase.getProductsByCategory(
                    categoryId!!,
                    page,
                    search
                )

                else -> {
                    _error.value = "Invalid navigation parameters"
                    _isLoading.value = false
                    return@launch
                }
            }
            result.fold(
                onSuccess = { response ->
                    if (page == 1) {
                        _products.value = response.products
                    } else {
                        val currentList = _products.value
                        _products.value = currentList + response.products
                    }
                    _currentPage.value = response.currentPage
                    _hasNextPage.value = response.hasNext
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Failed to load products"
                    _isLoading.value = false
                }
            )
        }
    }

    fun searchProducts(query: String) {
        _searchQuery.value = query
        loadProducts(search = query)
    }

    fun loadNextPage() {
        val currentPage = _currentPage.value
        val hasNext = _hasNextPage.value
        val searchQuery = _searchQuery.value

        if (hasNext && !_isLoading.value) {
            loadProducts(currentPage + 1, searchQuery)
        }
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            cartUseCase.addToCart(product, quantity).fold(
                onSuccess = {updatedCart ->
                    _addToCartSuccess.value = "${product.name} added to cart"
                    kotlinx.coroutines.delay(2000)
                    _addToCartSuccess.value = null
                },
                onFailure = {exception ->
                    _cartError.value = exception.message ?: "Failed to add to cart"
                }
            )
        }
    }

    fun clearCartError(){
        _cartError.value = null
    }
}