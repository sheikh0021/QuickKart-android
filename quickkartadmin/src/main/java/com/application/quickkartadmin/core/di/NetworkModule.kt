package com.application.quickkartadmin.core.di

import android.content.Context
import com.application.quickkartadmin.core.util.PreferencesManager
import com.application.quickkartadmin.data.remote.api.AdminApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor{
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    @Named("authenticatedOkHttpClient")
    fun providesOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        preferencesManager: PreferencesManager
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val token = preferencesManager.getToken()
                val request = if (token != null) {
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    chain.request()
                }
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    @Named("authenticatedRetrofit")
    fun provideRetrofit(@Named("authenticatedOkHttpClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(com.application.quickkartadmin.core.util.Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("unauthenticatedOkHttpClient")
    fun provideUnauthenticatedOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("unauthenticatedRetrofit")
    fun provideUnauthenticatedRetrofit(@Named("unauthenticatedOkHttpClient") unauthenticatedOkHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(com.application.quickkartadmin.core.util.Constants.BASE_URL)
            .client(unauthenticatedOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAdminApi(@Named("authenticatedRetrofit") retrofit: Retrofit): AdminApi{
        return retrofit.create(AdminApi::class.java)
    }

    @Provides
    @Singleton
    @Named("adminLoginApi")
    fun provideAdminLoginApi(@Named("unauthenticatedRetrofit") unauthenticatedRetrofit: Retrofit): AdminApi{
        return unauthenticatedRetrofit.create(AdminApi::class.java)
    }

    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager {
        return PreferencesManager(context)
    }
}