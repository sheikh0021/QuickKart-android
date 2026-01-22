package com.application.quickkartcustomer.presentation.tracking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartcustomer.data.remote.api.GoogleDirectionsApi
import com.application.quickkartcustomer.domain.model.AnimatedLocation
import com.application.quickkartcustomer.domain.model.DeliveryLocation
import com.application.quickkartcustomer.domain.model.DeliveryRoute
import com.application.quickkartcustomer.domain.model.TrackingPhase
import com.application.quickkartcustomer.domain.usecase.OrderUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OrderTrackingViewModel @Inject constructor(
    private val orderUseCase: OrderUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val orderId: Int = checkNotNull(savedStateHandle["orderId"])

    private val _deliveryLocation = MutableStateFlow<DeliveryLocation?>(null)
    val deliveryLocation: StateFlow<DeliveryLocation?> = _deliveryLocation

    private val _route = MutableStateFlow<DeliveryRoute?>(null)
    val route: StateFlow<DeliveryRoute?> = _route

    private val _animatedLocation = MutableStateFlow<AnimatedLocation?>(null)
    val animatedLocation: StateFlow<AnimatedLocation?> = _animatedLocation

    private val _trackingPhase = MutableStateFlow(TrackingPhase.PREPARING)
    val trackingPhase: StateFlow<TrackingPhase> = _trackingPhase

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean?> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking

    private val _customerLocation = MutableStateFlow<LatLng?>(null)
    val customerLocation : StateFlow<LatLng?> = _customerLocation

    private val _storeLocation = MutableStateFlow<LatLng?>(null)
    val storeLocation: StateFlow<LatLng?> = _storeLocation

    private val directionApi = GoogleDirectionsApi()
    private var lastFetchedLocation: LatLng? = null
    private var trackingJob: Job? = null
    private var animationJob: Job? = null

    private val LOCATION_UPDATE_INTERVAL = 5000L
    private val ANIMATION_DURATION = 5000L
    private val ROUTE_REFRESH_THRESHOLD = 100.0

    private val _lastUpdateTime = MutableStateFlow<Long?>(null)
    val lastUpdateTime: StateFlow<Long?> = _lastUpdateTime

    init {
        loadDeliveryLocation()
        viewModelScope.launch {
            _customerLocation.value = getCustomerLocation()
        }
    }

    fun loadDeliveryLocation(){
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            orderUseCase.getDeliveryLocation(orderId).fold(
                onSuccess = {location ->
                    _deliveryLocation.value = location
                    _lastUpdateTime.value = System.currentTimeMillis()
                    updateTrackingPhase(location)

                    location.location?.let { partnerLoc ->
                        val partnerLatLng = LatLng(partnerLoc.latitude, partnerLoc.longitude)
                        fetchRouteIfNeeded(partnerLatLng)
                    }
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Failed to load delivery location"
                    _isLoading.value = false
                }
            )
        }
    }

    fun startLiveTracking(){
        if (_isTracking.value) return
        _isTracking.value = true
        trackingJob = viewModelScope.launch {
            while (isActive && _isTracking.value){
                val currentLocation = _deliveryLocation.value
                if (currentLocation?.assignmentStatus?.delivered == true) {
                    stopLiveTracking()
                    break
                }
                loadDeliveryLocation()
                delay(LOCATION_UPDATE_INTERVAL)
            }
        }
    }

    fun stopLiveTracking(){
        _isTracking.value = false
        trackingJob?.cancel()
        animationJob?.cancel()
    }

    private fun fetchRouteIfNeeded(newLocation: LatLng){
        viewModelScope.launch {
            val shouldFetch = lastFetchedLocation == null ||
                    directionApi.calculateDistance(lastFetchedLocation!!, newLocation) >
                    ROUTE_REFRESH_THRESHOLD

            if (shouldFetch){
                val customerLocation = _customerLocation.value ?: getCustomerLocation()

                directionApi.getDirections(newLocation, customerLocation).fold(
                    onSuccess = {route ->
                        _route.value = route
                        lastFetchedLocation = newLocation
                        startMarkerAnimation(newLocation)
                    },
                    onFailure = {error ->
                        _route.value = null
                    }
                )
            } else {
                startMarkerAnimation(newLocation)
            }
        }
    }

    private fun startMarkerAnimation(targetPosition: LatLng){
        animationJob?.cancel()

        val currentPosition = _animatedLocation.value?.currentPosition
            ?: targetPosition

        if (currentPosition == targetPosition) return

        val bearing = directionApi.calculateBearing(currentPosition, targetPosition)

        animationJob = viewModelScope.launch {
            val steps = 50
            val delayPerStep = ANIMATION_DURATION / steps

            for (i in 0..steps){
                if (!isActive) break

                val progress = i.toFloat() / steps
                val lat = currentPosition.latitude +
                        (targetPosition.latitude - currentPosition.latitude) * progress
                val lng = currentPosition.longitude +
                        (targetPosition.longitude - currentPosition.longitude) * progress

                _animatedLocation.value = AnimatedLocation(
                    currentPosition = LatLng(lat, lng),
                    targetPosition = targetPosition,
                    progress = progress,
                    bearing = bearing
                )
                delay(delayPerStep)
            }
            _animatedLocation.value = AnimatedLocation(
                currentPosition = targetPosition,
                targetPosition = targetPosition,
                progress = 1f,
                bearing = bearing
            )
        }
    }

    private fun updateTrackingPhase(location: DeliveryLocation){
        val status = location.assignmentStatus

        _trackingPhase.value = when {
            status.delivered -> TrackingPhase.DELIVERED
            status.outForDelivery -> {
                val eta = _route.value?.etaMinutes ?: Int.MAX_VALUE
                if (eta <= 5) TrackingPhase.ARRIVING_SOON else TrackingPhase.ON_THE_WAY
            }
            status.pickedUp -> TrackingPhase.ON_THE_WAY
            else -> TrackingPhase.PREPARING
        }
    }

    fun refreshLocation(){
        loadDeliveryLocation()
    }

    private suspend fun getCustomerLocation(): LatLng{
        return try {
            orderUseCase.getOrderById(orderId).fold(
                onSuccess = {order ->
                    if (order.deliveryLatitude !=null && order.deliveryLongitude != null){
                        LatLng(order.deliveryLatitude, order.deliveryLongitude)
                    } else {
                        LatLng(21.6139, 77.2090)
                    }
                },
                onFailure = {
                    LatLng(28.6139, 77.2090)
                }
            )
        } catch (e: Exception){
            LatLng(28.6139, 77.2090)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopLiveTracking()
    }
}