package com.tstreet.onhand.core.network.di

import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.fake.FakeOnHandNetworkDataSource
import com.tstreet.onhand.core.network.retrofit.RetrofitOnHandNetwork
import dagger.Binds
import dagger.Module

@Module
interface NetworkModule {

    // For real network data source
    // @Binds
    // fun RetrofitOnHandNetwork.binds() : OnHandNetworkDataSource

    // For fake network data source
    @Binds
    fun FakeOnHandNetworkDataSource.binds() : OnHandNetworkDataSource

    // FYI: Above is shorthand of this:
    //    @Binds
    //    fun provideOnHandNetworkDataSource(
    //        src: RetrofitOnHandNetwork
    //    ): OnHandNetworkDataSource
}