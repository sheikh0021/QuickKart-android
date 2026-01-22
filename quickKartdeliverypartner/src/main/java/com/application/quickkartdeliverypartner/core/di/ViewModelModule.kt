package com.application.quickkartdeliverypartner.core.di

import com.application.quickkartdeliverypartner.domain.repository.AuthRepository
import com.application.quickkartdeliverypartner.domain.repository.DeliveryRepository
import com.application.quickkartdeliverypartner.domain.repository.ChatRepository
import com.application.quickkartdeliverypartner.domain.usecase.AuthUseCase
import com.application.quickkartdeliverypartner.domain.usecase.DeliveryUseCase
import com.application.quickkartdeliverypartner.domain.usecase.ChatUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    fun provideAuthUseCase(authRepository: AuthRepository): AuthUseCase {
        return AuthUseCase(authRepository)
    }

    @Provides
    fun provideDeliveryUseCase(deliveryRepository: DeliveryRepository): DeliveryUseCase {
        return DeliveryUseCase(deliveryRepository)
    }

    @Provides
    fun provideChatUseCase(chatRepository: ChatRepository): ChatUseCase {
        return ChatUseCase(chatRepository)
    }
}