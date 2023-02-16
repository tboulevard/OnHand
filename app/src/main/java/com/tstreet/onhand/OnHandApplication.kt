package com.tstreet.onhand

import android.app.Application
import com.tstreet.onhand.core.data.di.DaggerDataComponent
import com.tstreet.onhand.core.data.di.DataComponent
import dagger.Component

// appComponent lives in the Application class to share its lifecycle
class OnHandApplication : Application() {

    // Reference to the application graph that is used across the whole app
    // Instance of the AppComponent that will be used by all the Activities in the project
//    val appComponent by lazy {
//        DaggerOnHandApplicationComponent.factory().create(applicationContext)
//    }

    lateinit var appComponent: OnHandApplicationComponent
        private set

    lateinit var dataComponent: DataComponent
        private set

    override fun onCreate() {
        super.onCreate()
        dataComponent = DaggerDataComponent
            .builder()
            .build()
        appComponent = DaggerOnHandApplicationComponent
            .builder()
            .dataComponent(dataComponent)
            .build()
    }
}

val Application.appComponent: OnHandApplicationComponent
    get() = (this as OnHandApplication).appComponent

val Application.dataComponent: DataComponent
    get() = (this as OnHandApplication).dataComponent