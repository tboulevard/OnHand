package com.tstreet.onhand

import com.tstreet.onhand.core.network.di.NetworkModule
import com.tstreet.onhand.core.data.di.DataModule
import dagger.Component
import javax.inject.Singleton

/**
 * Put things in here to stay alive for lifetime of application (until killed or cleaned up)
 */
@Singleton
@Component(
    modules = [
        // TODO: invert control later, this is messy and has tight coupling
        // TODO: https://developer.android.com/training/dependency-injection/dagger-multi-module
        NetworkModule::class,
        DataModule::class
    ]
)
interface OnHandApplicationComponent