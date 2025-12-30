package com.application.quickkartcustomer.core.util


object Constants {
    const val BASE_URL = "http://10.0.2.2:8000/api" //for android emulator
    // const val BASE_URL = "http://192.168.1.100:8000/api/" //replace when using with physical devices

    const val PREFS_TOKEN = "prefs_token"
    const val PREFS_USER = "prefs_user"

    //api endpoints
    const val ENDPOINT_LOGIN = "users/login/"
    const val ENDPOINT_REGISTER = "users/register/"
    const val ENDPOINT_STORES = "stores/"
    const val ENDPOINT_PRODUCTS = "products/"
    const val ENDPOINT_ORDERS = "orders/"
    const val ENDPOINT_CATEGORIES = "product/categories/"
}