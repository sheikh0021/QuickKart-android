package com.application.quickkartcustomer.core.di

import com.application.quickkartcustomer.data.mapper.AuthMapper
import com.application.quickkartcustomer.data.mapper.OrderMapper
import com.application.quickkartcustomer.data.mapper.ProductMapper
import com.application.quickkartcustomer.data.mapper.StoreMapper
import com.application.quickkartcustomer.data.remote.api.AuthApi
import com.application.quickkartcustomer.data.remote.api.OrderApi
import com.application.quickkartcustomer.data.remote.api.StoreApi
import com.application.quickkartcustomer.data.repository.AuthRepositoryImpl
import com.application.quickkartcustomer.data.repository.CartRepositoryImpl
import com.application.quickkartcustomer.data.repository.OrderRepositoryImpl
import com.application.quickkartcustomer.data.repository.ProductRepositoryImpl
import com.application.quickkartcustomer.data.repository.StoreRepositoryImpl
import com.application.quickkartcustomer.domain.repository.AuthRepository
import com.application.quickkartcustomer.domain.repository.CartRepository
import com.application.quickkartcustomer.domain.repository.OrderRepository
import com.application.quickkartcustomer.domain.repository.ProductRepository
import com.application.quickkartcustomer.domain.repository.StoreRepository
import com.application.quickkartcustomer.domain.usecase.AuthUseCase
import com.application.quickkartcustomer.domain.usecase.CartUseCase
import com.application.quickkartcustomer.domain.usecase.OrderUseCase
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

    @Provides
    fun provideCartRepository(): CartRepository = CartRepositoryImpl()

    //mappers these are
    @Provides
    fun provideAuthMapper() : AuthMapper = AuthMapper()

    @Provides
    fun provideStoreMapper(): StoreMapper = StoreMapper()

    @Provides
    fun provideProductMapper(): ProductMapper = ProductMapper()

    @Provides
    fun provideOrderMapper(): OrderMapper = OrderMapper()


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
    fun providesCartUseCase(cartRepository: CartRepository): CartUseCase = CartUseCase(cartRepository)

    @Provides
    fun provideProductRepository(
        storeApi: StoreApi,
        productMapper: ProductMapper
    ): ProductRepository = ProductRepositoryImpl(storeApi, productMapper)

    @Provides
    fun provideOrderRepository(
        orderApi: OrderApi,
        orderMapper: OrderMapper
    ): OrderRepository = OrderRepositoryImpl(orderApi, orderMapper)

    @Provides
    fun provideOrderUseCase(orderRepository: OrderRepository): OrderUseCase = OrderUseCase(orderRepository)

}