package com.tstreet.onhand

import android.app.Application
import com.tstreet.onhand.core.common.DaggerCommonComponent
import com.tstreet.onhand.core.data.impl.di.DaggerDataComponent

class OnHandApplication : Application() {

    // Singleton reference to the application graph that is used across the whole app
    lateinit var appComponent: OnHandApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()

        val commonComponent = DaggerCommonComponent.factory().create(this)
        val dataComponent = DaggerDataComponent
            .builder()
            .commonComponentProvider(commonComponent)
            .build()
        appComponent = DaggerOnHandApplicationComponent
            .builder()
            .commonComponentProvider(commonComponent)
            .dataComponentProvider(dataComponent)
            .build()
    }
}

val Application.appComponent: OnHandApplicationComponent
    get() = (this as OnHandApplication).appComponent