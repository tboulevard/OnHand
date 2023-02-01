package com.tstreet.onhand

import com.tstreet.onhand.core.network.di.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        // TODO: invert control later
        NetworkModule::class
    ]
)
interface OnHandApplicationComponent