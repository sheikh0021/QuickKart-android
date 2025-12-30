package com.application.quickkartcustomer.core.network

import com.application.quickkartcustomer.core.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getClient(token: String? = null): Retrofit {
if (retrofit == null) {
    val interceptor = HttpLoggingInterceptor()
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor).addInterceptor { chain ->
            val request = chain.request().newBuilder()
            token?.let {
                request.addHeader("Authorization", "Bearer $it")
            }
            chain.proceed(request.build())
        }
        .build()

    retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(
        GsonConverterFactory.create()).client(client).build()
}
        return retrofit!!
    }
}