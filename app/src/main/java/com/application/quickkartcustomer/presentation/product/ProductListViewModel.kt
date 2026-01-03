package com.application.quickkartcustomer.presentation.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartcustomer.domain.model.Product
import com.application.quickkartcustomer.domain.usecase.ProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productUseCase: ProductUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _currentPage = MutableLiveData<Int>()
    val currentPage: LiveData<Int> = _currentPage

    private val _hasNextPage = MutableLiveData<Boolean>()
    val hasNextPage: LiveData<Boolean> = _hasNextPage

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    private var storeId: Int? = null
    private var categoryId: Int? = null

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
                        val currentList = _products.value ?: emptyList()
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
        val currentPage = _currentPage.value ?: 1
        val hasNext = _hasNextPage.value ?: false
        val searchQuery = _searchQuery.value

        if (hasNext && !_isLoading.value!!) {
            loadProducts(currentPage + 1, searchQuery)
        }
    }

}