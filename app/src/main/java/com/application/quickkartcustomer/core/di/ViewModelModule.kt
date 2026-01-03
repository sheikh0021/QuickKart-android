package com.application.quickkartcustomer.core.di

import com.application.quickkartcustomer.data.mapper.AuthMapper
import com.application.quickkartcustomer.data.mapper.ProductMapper
import com.application.quickkartcustomer.data.mapper.StoreMapper
import com.application.quickkartcustomer.data.remote.api.AuthApi
import com.application.quickkartcustomer.data.remote.api.StoreApi
import com.application.quickkartcustomer.data.repository.AuthRepositoryImpl
import com.application.quickkartcustomer.data.repository.ProductRepositoryImpl
import com.application.quickkartcustomer.data.repository.StoreRepositoryImpl
import com.application.quickkartcustomer.domain.repository.AuthRepository
import com.application.quickkartcustomer.domain.repository.ProductRepository
import com.application.quickkartcustomer.domain.repository.StoreRepository
import com.application.quickkartcustomer.domain.usecase.AuthUseCase
import com.application.quickkartcustomer.domain.usecase.ProductUseCase
import com.application.quickkartcustomer.domain.usecase.StoreUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    fun provideProductUseCase(productRepository: ProductRepository): ProductUseCase =
        ProductUseCase(productRepository)

    //mappers these are
    @Provides
    fun provideAuthMapper() : AuthMapper = AuthMapper()

    @Provides
    fun provideStoreMapper(): StoreMapper = StoreMapper()

    @Provides
    fun provideProductMapper(): ProductMapper = ProductMapper()


    //repository
    @Provides
    fun provideAuthRepository(
        authApi: AuthApi,
        authMapper: AuthMapper
    ): AuthRepository {
        return AuthRepositoryImpl(authApi, authMapper)
    }

    @Provides
    fun provideStoreRepository(
        storeApi: StoreApi,
        storeMapper: StoreMapper
    ) : StoreRepository = StoreRepositoryImpl(storeApi, storeMapper)

    @Provides
    fun provideAuthUseCase(authRepository: AuthRepository) : AuthUseCase = AuthUseCase(authRepository)

    @Provides
    fun provideStoreUseCase(storeRepository: StoreRepository): StoreUseCase = StoreUseCase(storeRepository)

    @Provides
    fun provideProductRepository(
        storeApi: StoreApi,
        productMapper: ProductMapper
    ): ProductRepository = ProductRepositoryImpl(storeApi, productMapper)

}