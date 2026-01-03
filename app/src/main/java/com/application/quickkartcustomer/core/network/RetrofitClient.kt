package com.application.quickkartcustomer.core.network

import com.application.quickkartcustomer.core.util.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private var retrofit: Retrofit? = null
    private var authenticatedRetrofit: Retrofit? = null

    //unauthenticated retrofit (for login/register)
    fun getClient(): Retrofit{
        if (retrofit == null) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        return retrofit!!
    }

    //Authenticated client(for protected endpoints)
    fun getAuthenticatedClient(token: String?): Retrofit {
        if (authenticatedRetrofit == null || token != null) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val authInterceptor = Interceptor {chain ->
                val original = chain.request()
                val request = if (!token.isNullOrEmpty()) {
                    original.newBuilder().header("Authorization", "Bearer $token").build()
                } else {
                    original
                }
                chain.proceed(request)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(authInterceptor)
                .build()

            authenticatedRetrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(
                GsonConverterFactory.create()).client(client).build()
        }
        return authenticatedRetrofit!!
    }
//legacy method for backward compatibility
fun getClient(token: String? = null): Retrofit {
    return if (token != null) {
        getAuthenticatedClient(token)
    }else {
        getClient()
    }
}
}