package com.application.quickkartcustomer.ui.navigation

import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun NavController.navigateWithAnimation(
    route: String,
    navigationStateManager: NavigationStateManager
){
    navigationStateManager.startNavigating()

    CoroutineScope(Dispatchers.Main).launch {
        delay(2000)
        this@navigateWithAnimation.navigate(route){
            //popUpTo(Screen.Home.route) {inclusive=false}
        }
        delay(100)
        navigationStateManager.stopNavigating()
    }
}

fun NavController.navigateWithAnimation(
    route: String,
    navigationStateManager: NavigationStateManager,
    builder: androidx.navigation.NavOptionsBuilder.() -> Unit
){
    navigationStateManager.startNavigating()

    CoroutineScope(Dispatchers.Main).launch {
        delay(2000)
        this@navigateWithAnimation.navigate(route, builder)

        delay(100)
        navigationStateManager.stopNavigating()
    }
}