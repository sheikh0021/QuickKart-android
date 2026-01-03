package com.application.quickkartcustomer.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartcustomer.domain.model.HomeData
import com.application.quickkartcustomer.domain.model.Store
import com.application.quickkartcustomer.domain.usecase.StoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storeUseCase: StoreUseCase
): ViewModel() {
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _homeData = MutableStateFlow<HomeData?>(null)
    val homeData: StateFlow<HomeData?> = _homeData

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadStores()
        loadHomeData()
    }

    private fun loadStores() {
        viewModelScope.launch {
            _isLoading.value = true
            storeUseCase.getNearbyStores().fold(
                onSuccess = {stores ->
                    _stores.value = stores
                    _isLoading.value = false
                },
                onFailure = {exception ->
                    _isLoading.value = false
                }
            )
        }
    }
    fun loadHomeData(){
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            storeUseCase.getHomeData().fold(
                onSuccess = {data ->
                    _homeData.value = data
                    _isLoading.value = false
                },
                onFailure = {exception ->
                    val errorMessage = when {
                        exception.message?.contains("401") ==true || exception.message?.contains("Unauthorized") == true -> {
                            "Authentication failed. Please login again."
                        }
                        exception.message?.contains("404") == true -> {
                            "Home data not available.Using basic store list."
                        }
                        else -> exception.message ?: "Failed to load home data"
                    }
                    _error.value = errorMessage
                    _isLoading.value = false
                }
            )
        }
    }
}
