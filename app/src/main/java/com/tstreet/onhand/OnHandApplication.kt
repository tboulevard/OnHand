package com.tstreet.onhand

import android.app.Application
import com.tstreet.onhand.core.common.DaggerContextComponent
import com.tstreet.onhand.core.data.di.DaggerDataComponent

class OnHandApplication : Application() {

    // Singleton reference to the application graph that is used across the whole app
    lateinit var appComponent: OnHandApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()

        val contextComponent = DaggerContextComponent.factory().create(this)
        val dataComponent = DaggerDataComponent
            .builder()
            .contextComponentProvider(contextComponent)
            .build()
        appComponent = DaggerOnHandApplicationComponent
            .builder()
            .contextComponentProvider(contextComponent)
            .dataComponentProvider(dataComponent)
            .build()
    }
}

val Application.appComponent: OnHandApplicationComponent
    get() = (this as OnHandApplication).appComponent