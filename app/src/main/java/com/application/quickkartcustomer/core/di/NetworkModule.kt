package com.application.quickkartcustomer.core.di

import com.application.quickkartcustomer.core.network.RetrofitClient
import com.application.quickkartcustomer.core.util.PreferencesManager
import com.application.quickkartcustomer.data.remote.api.AuthApi
import com.application.quickkartcustomer.data.remote.api.StoreApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(preferencesManager: PreferencesManager): Retrofit {
        val token = preferencesManager.getToken()
        return RetrofitClient.getClient(token)
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
    @Provides
    @Singleton
    fun provideStoreApi(retrofit: Retrofit): StoreApi {
        return retrofit.create(StoreApi::class.java)
    }

}