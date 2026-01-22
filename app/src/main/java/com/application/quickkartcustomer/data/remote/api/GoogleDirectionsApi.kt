package com.application.quickkartcustomer.data.remote.api

import com.application.quickkartcustomer.domain.model.DeliveryRoute
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder


class GoogleDirectionsApi {

    companion object{
        private const val API_KEY = "AIzaSyDyxHqG8kIvExJqr4w4CbhsaabU6WjPf_I"
        private const val BASE_URL = "https://maps.googleapis.com/maps/api/directions/json"
    }

    suspend fun getDirections(
        origin: LatLng,
        destination: LatLng
    ) : Result<DeliveryRoute> = withContext(Dispatchers.IO) {
        try {
            val originStr = "${origin.latitude},${origin.longitude}"
            val destinationStr = "${destination.latitude}, ${destination.longitude}"

            val urlString = "$BASE_URL" +
                    "origin=${URLEncoder.encode(originStr, "UTF-8")}&" +
                    "destination=${URLEncoder.encode(destinationStr, "UTF-8")}&" +
                    "mode=driving&" +
                    "key=$API_KEY"

            val response = URL(urlString).readText()
            val json = JSONObject(response)

            if (json.getString("status") == "OK") {
                val routes = json.getJSONArray("routes")
                if (routes.length() > 0) {
                    val route = routes.getJSONObject(0)
                    val legs = route.getJSONArray("legs").getJSONObject(0)

                    val distance = legs.getJSONObject("distance")
                    val distanceMeters = distance.getInt("value")
                    val distanceText = distance.getString("text")

                    val duration = legs.getJSONObject("duration")
                    val durationSeconds = duration.getInt("value")
                    val durationText = duration.getString("text")

                    val polylinePoints = decodePolyline(
                        route.getJSONObject("overview_polyline").getString("points")
                    )
                    val deliveryRoute = DeliveryRoute(
                        polylinePoints = polylinePoints,
                        distanceMeters = distanceMeters.toDouble(),
                        distanceText = distanceText,
                        durationSeconds = durationSeconds,
                        durationText = durationText,
                        etaMinutes = (durationSeconds/ 60)
                    )
                    Result.success(deliveryRoute)
                } else {
                    Result.failure(Exception("No routes found"))
                }
            } else {
                Result.failure(Exception("Directions API error: ${json.getString("status")}"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    private fun decodePolyline(encoded: String): List<LatLng>{
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(latLng)
        }
        return poly
    }

    fun calculateDistance(point1: LatLng, point2: LatLng): Double {
        val earthRadius = 6371000.0

        val lat1Rad = Math.toRadians(point1.latitude)
        val lat2Rad = Math.toRadians(point2.latitude)
        val deltaLat = Math.toRadians(point2.latitude - point1.latitude)
        val deltaLng = Math.toRadians(point2.longitude - point1.longitude)

        val a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                Math.sin(deltaLng / 2) * Math.sin(deltaLng)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return earthRadius * c
    }
    fun calculateBearing(start: LatLng, end: LatLng): Float {
        val lat1 = Math.toRadians(start.latitude)
        val lat2 = Math.toRadians(end.latitude)
        val deltaLng = Math.toRadians(end.longitude - start.longitude)

        val y = Math.sin(deltaLng) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLng)

        val bearing = Math.toDegrees(Math.atan2(y, x))
        return ((bearing + 360) % 360).toFloat()
    }
}