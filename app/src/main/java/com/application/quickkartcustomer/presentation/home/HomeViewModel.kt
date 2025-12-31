package com.application.quickkartcustomer.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadStores()
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
}
