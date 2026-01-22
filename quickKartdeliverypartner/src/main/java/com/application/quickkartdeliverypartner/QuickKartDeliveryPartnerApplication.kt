package com.application.quickkartdeliverypartner

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DeliveryPartnerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // App initialization
    }
}