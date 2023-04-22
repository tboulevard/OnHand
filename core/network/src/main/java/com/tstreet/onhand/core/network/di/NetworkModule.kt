package com.tstreet.onhand.core.network.di

import com.tstreet.onhand.core.network.BuildConfig
import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.fake.FakeOnHandNetworkDataSource
import com.tstreet.onhand.core.network.retrofit.RetrofitOnHandNetwork
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
object NetworkModule {

    // TODO: extract to build config later
    private const val USE_FAKE_DATASOURCE = BuildConfig.useFakeDataSource

    @Provides
    @Singleton
    fun providesRetrofitOnHandNetwork(
        networkJson: Json
    ): OnHandNetworkDataSource {
        if (USE_FAKE_DATASOURCE) {
            return FakeOnHandNetworkDataSource()
        }
        return RetrofitOnHandNetwork(networkJson)
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