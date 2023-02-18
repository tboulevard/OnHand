package com.tstreet.onhand

import android.app.Application
import com.tstreet.onhand.core.data.di.DaggerDataComponent
import com.tstreet.onhand.core.data.di.DataComponent

class OnHandApplication : Application() {

    // Singleton reference to the application graph that is used across the whole app
    lateinit var appComponent: OnHandApplicationComponent
        private set

    // Singleton reference to the data graph that is used across the whole app. The intent is for
    // this graph to only be built once, as multiple features will use its modules.
    lateinit var dataComponent: DataComponent
        private set

    override fun onCreate() {
        super.onCreate()

        dataComponent = DaggerDataComponent
            .builder()
            .build()

        appComponent = DaggerOnHandApplicationComponent
            .builder()
            .dataComponentProvider(dataComponent)
            .build()
    }
}

val Application.appComponent: OnHandApplicationComponent
    get() = (this as OnHandApplication).appComponent

// TODO: Cleanup later, messy to expose data component via Application class
val Application.dataComponent: DataComponent
    get() = (this as OnHandApplication).dataComponent