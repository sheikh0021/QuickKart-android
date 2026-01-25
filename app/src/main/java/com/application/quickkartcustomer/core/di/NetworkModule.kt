package com.application.quickkartcustomer.core.di

import com.application.quickkartcustomer.core.network.RetrofitClient
import com.application.quickkartcustomer.core.util.PreferencesManager
import com.application.quickkartcustomer.data.repository.CartRepositoryImpl
import com.application.quickkartcustomer.data.repository.ChatRepositoryImpl
import com.application.quickkartcustomer.domain.repository.CartRepository
import com.application.quickkartcustomer.domain.repository.ChatRepository
import com.application.quickkartcustomer.data.remote.api.AuthApi
import com.application.quickkartcustomer.data.remote.api.DeliveryApi
import com.application.quickkartcustomer.data.remote.api.OrderApi
import com.application.quickkartcustomer.data.remote.api.ProfileApi
import com.application.quickkartcustomer.data.remote.api.StoreApi
import com.application.quickkartcustomer.data.remote.api.ChatApi
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return RetrofitClient.getClient()
    }

    @Provides
    @Singleton
    @Named("authenticated")
    fun provideAuthenticatedRetrofit(preferencesManager: PreferencesManager): Retrofit {
        return RetrofitClient.getAuthenticatedClient(preferencesManager)
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
    @Provides
    @Singleton
    fun provideStoreApi(@Named("authenticated") retrofit: Retrofit): StoreApi {
        return retrofit.create(StoreApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOrderApi(@Named("authenticated") retrofit: Retrofit): OrderApi {
        return retrofit.create(OrderApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileApi(@Named("authenticated") retrofit: Retrofit): ProfileApi {
        return retrofit.create(ProfileApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDeliveryApi(@Named("authenticated") retrofit: Retrofit): DeliveryApi {
        return retrofit.create(DeliveryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCartRepository(): CartRepository{
        return CartRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideChatApi(@Named("authenticated") retrofit: Retrofit): ChatApi {
        return retrofit.create(ChatApi::class.java)
    }

    // REMOVED: ChatRepository is now provided in ViewModelModule to avoid duplicate bindings

}