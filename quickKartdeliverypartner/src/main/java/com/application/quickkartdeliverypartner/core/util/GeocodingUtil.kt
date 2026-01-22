package com.application.quickkartdeliverypartner.core.util

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder


object GeocodingUtil {
private const val API_KEY = "AIzaSyDyxHqG8kIvExJqr4w4CbhsaabU6WjPf_I"
private const val BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json"

    suspend fun geocodeAddress(address: String): Result<LatLng> = withContext(Dispatchers.IO) {
        try {
            val encodedAddress = URLEncoder.encode(address, "UTF-8")
            val urlString = "$BASE_URL?address=$encodedAddress&key=$API_KEY"

            val response = URL(urlString).readText()
            val json = JSONObject(response)

            if (json.getString("status") == "OK") {
                val results = json.getJSONArray("results")
                if (results.length() > 0) {
                    val location = results.getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONObject("location")

                    val lat = location.getDouble("lat")
                    val lng = location.getDouble("lng")

                    Result.success(LatLng(lat, lng))
                } else {
                    Result.failure(Exception("No results found"))
                }
            } else {
                Result.failure(Exception("Geocoding failed: ${json.getString("status")}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}