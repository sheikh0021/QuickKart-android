package com.application.quickkartcustomer.core.di

import com.application.quickkartcustomer.data.mapper.AuthMapper
import com.application.quickkartcustomer.data.remote.api.AuthApi
import com.application.quickkartcustomer.data.repository.AuthRepositoryImpl
import com.application.quickkartcustomer.domain.repository.AuthRepository
import com.application.quickkartcustomer.domain.usecase.AuthUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    fun provideAuthMapper() : AuthMapper {
        return AuthMapper()
    }

    @Provides
    fun provideAuthRepository(
        authApi: AuthApi,
        authMapper: AuthMapper
    ): AuthRepository {
        return AuthRepositoryImpl(authApi, authMapper)
    }

    @Provides
    fun provideAuthUseCase(authRepository: AuthRepository): AuthUseCase {
        return AuthUseCase(authRepository)
    }
}