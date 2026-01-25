package com.application.quickkartcustomer.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NavigationStateManager @Inject constructor() {
    var isNavigating: Boolean by mutableStateOf(false)
        private set

    fun startNavigating(){
        isNavigating = true
    }
    fun stopNavigating(){
        isNavigating = false
    }
}