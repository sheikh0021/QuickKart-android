package com.application.quickkartadmin.core.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.Exception
import javax.inject.Inject

class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson = Gson()
)  {
    private val prefs: SharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,
        Context.MODE_PRIVATE)

    fun saveToken(token: String){
        prefs.edit().putString(Constants.TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(Constants.TOKEN_KEY, null)
    }

    fun saveUserData(userData: Map<String, Any>){
        val json = gson.toJson(userData)
        prefs.edit().putString(Constants.USER_DATA_KEY, json).apply()
    }

    fun getUserData(): Map<String, Any>? {
        val json =  prefs.getString(Constants.USER_DATA_KEY, null)
        return if (json != null) {
            try {
                @Suppress("UNCHECKED_CAST")
                gson.fromJson(json, Map::class.java) as? Map<String, Any>
            } catch (e: Exception) {
                null
            }
        } else null
    }

    fun clearAllData(){
        prefs.edit().clear().apply()
    }
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

}