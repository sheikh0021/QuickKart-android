package com.application.quickkartadmin.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartadmin.domain.model.DashboardStats
import com.application.quickkartadmin.domain.usecase.AdminUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val adminUseCase: AdminUseCase
): ViewModel() {
    private val _dashboardStats = MutableStateFlow<DashboardStats?>(null)
    val dashboardStats: StateFlow<DashboardStats?> = _dashboardStats

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            adminUseCase.getDashboardStats().fold(
                onSuccess = {stats ->
                    _dashboardStats.value = stats
                    _isLoading.value = false
                },
                onFailure = {exception ->
                    _error.value = exception.message?: "Failed to load dashboard stats"
                    _isLoading.value = false
                }
            )
        }
    }
}