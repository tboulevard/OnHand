package com.tstreet.onhand.core.network.di

import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.fake.FakeOnHandNetworkDataSource
import com.tstreet.onhand.core.network.retrofit.RetrofitOnHandNetwork
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface NetworkModule {

    // For real network data source
    // @Binds
    // fun RetrofitOnHandNetwork.binds() : OnHandNetworkDataSource

    // For fake network data source
    @Binds
    @Singleton
    fun FakeOnHandNetworkDataSource.binds() : OnHandNetworkDataSource

    // FYI: Above is shorthand of this:
    //    @Binds
    //    @Singleton
    //    fun provideOnHandNetworkDataSource(
    //        src: RetrofitOnHandNetwork
    //    ): OnHandNetworkDataSource
}