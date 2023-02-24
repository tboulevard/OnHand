package com.tstreet.onhand.core.network.di

import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.fake.FakeOnHandNetworkDataSource
import com.tstreet.onhand.core.network.retrofit.RetrofitOnHandNetwork
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
object NetworkModule {

    // For real network data source
//    @Provides
//    fun providesRetrofitOnHandNetwork(
//        networkJson: Json
//    ): OnHandNetworkDataSource {
//        return RetrofitOnHandNetwork(networkJson)
//    }
//

    // For fake network data source
    @Provides
    @Singleton
    fun providesFakeOnHandNetworkDataSource() : OnHandNetworkDataSource {
        return FakeOnHandNetworkDataSource()
    }

    @Provides
    @Singleton
    // TODO: Needed for kotlin serialization, look into why later. Copied from Google NIA project
    fun providesNetworkJson(): Json = Json { ignoreUnknownKeys = true }

    // FYI: Above is shorthand of this:
    //    @Binds
    //    @Singleton
    //    fun provideOnHandNetworkDataSource(
    //        src: RetrofitOnHandNetwork
    //    ): OnHandNetworkDataSource
}