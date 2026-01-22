package com.application.quickkartdeliverypartner.core.di

import android.content.Context
import com.application.quickkartdeliverypartner.core.network.RetrofitClient
import com.application.quickkartdeliverypartner.core.util.PreferencesManager
import com.application.quickkartdeliverypartner.data.mapper.AuthMapper
import com.application.quickkartdeliverypartner.data.mapper.DeliveryMapper
import com.application.quickkartdeliverypartner.data.remote.api.AuthApi
import com.application.quickkartdeliverypartner.data.remote.api.DeliveryApi
import com.application.quickkartdeliverypartner.data.remote.api.ChatApi
import com.application.quickkartdeliverypartner.data.repository.AuthRepositoryImpl
import com.application.quickkartdeliverypartner.data.repository.DeliveryRepositoryImpl
import com.application.quickkartdeliverypartner.domain.repository.AuthRepository
import com.application.quickkartdeliverypartner.domain.repository.DeliveryRepository
import com.application.quickkartdeliverypartner.domain.repository.ChatRepository
import com.application.quickkartdeliverypartner.data.repository.ChatRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnauthenticatedRetrofit


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    @UnauthenticatedRetrofit
    fun provideRetrofit(): Retrofit {
        return RetrofitClient.getClient()
    }

    @Provides
    @Singleton
    @AuthenticatedRetrofit
    fun provideAuthenticatedRetrofit(preferencesManager: PreferencesManager): Retrofit {
        RetrofitClient.init(preferencesManager)
        return RetrofitClient.getAuthenticatedClient()
    }

    @Provides
    @Singleton
    fun provideAuthApi(@UnauthenticatedRetrofit retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDeliveryApi(@AuthenticatedRetrofit authenticatedRetrofit: Retrofit): DeliveryApi {
        return authenticatedRetrofit.create(DeliveryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChatApi(@AuthenticatedRetrofit authenticatedRetrofit: Retrofit): ChatApi {
        return authenticatedRetrofit.create(ChatApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApi,
        authMapper: AuthMapper
    ): AuthRepository {
        return AuthRepositoryImpl(authApi, authMapper)
    }

    @Provides
    @Singleton
    fun provideDeliveryRepository(
        deliveryApi: DeliveryApi,
        deliveryMapper: DeliveryMapper
    ): DeliveryRepository{
        return DeliveryRepositoryImpl(deliveryApi, deliveryMapper)
    }

    @Provides
    @Singleton
    fun provideAuthMapper(): AuthMapper {
        return AuthMapper()
    }

    @Provides
    @Singleton
    fun provideDeliveryMapper(): DeliveryMapper{
        return DeliveryMapper()
    }

    @Provides
    @Singleton
    fun provideChatRepository(chatApi: ChatApi): ChatRepository {
        return ChatRepositoryImpl(chatApi)
    }

}