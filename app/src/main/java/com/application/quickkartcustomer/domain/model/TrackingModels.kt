package com.application.quickkartcustomer.domain.model

import com.google.android.gms.maps.model.LatLng


data class  DeliveryRoute(
    val polylinePoints: List<LatLng>,
    val distanceMeters: Double,
    val distanceText: String,
    val durationSeconds: Int,
    val durationText: String,
    val etaMinutes: Int
)

data class AnimatedLocation(
    val currentPosition: LatLng,
    val targetPosition: LatLng,
    val progress: Float  = 0f,
    val bearing: Float = 0f
)

enum class TrackingPhase{
    PREPARING,
    ON_THE_WAY,
    ARRIVING_SOON,
    DELIVERED
}

data class TrackingState(
    val deliveryLocation: DeliveryLocation,
    val route: DeliveryRoute? = null,
    val animatedLocation: AnimatedLocation? = null,
    val phase: TrackingPhase,
    val shouldStopTracking: Boolean = false
)
