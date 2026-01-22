package com.application.quickkartcustomer.core.util

import android.content.Context
import android.content.SharedPreferences
import com.application.quickkartcustomer.domain.model.User
import com.google.gson.Gson

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("quickkart_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveToken(token: String) {
        prefs.edit().putString("access_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("access_token", null)
    }

    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        prefs.edit().putString("user_data", userJson).apply()
    }

    fun getUser(): User? {
        val userJson = prefs.getString("user_data", null)
        return if (userJson != null) {
            try {
                gson.fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    fun clearData() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
    fun isTokenValid(): Boolean{
        val token = getToken()
        return !token.isNullOrEmpty() && token.length >10
    }
    
    fun getAccessToken(): String? {
        return getToken()
    }
 }