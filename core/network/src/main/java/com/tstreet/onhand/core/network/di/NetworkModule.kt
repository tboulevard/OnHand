package com.tstreet.onhand.core.network.di

import OnHandNetworkDataSource
import dagger.Binds
import dagger.Module

@Module
abstract class NetworkModule {

    @Binds
    abstract fun provideRetrofitOnHandService() : OnHandNetworkDataSource
}