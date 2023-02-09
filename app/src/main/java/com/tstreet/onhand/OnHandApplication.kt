package com.tstreet.onhand

import android.app.Application

// appComponent lives in the Application class to share its lifecycle
class OnHandApplication : Application() {

    // Reference to the application graph that is used across the whole app
    // Instance of the AppComponent that will be used by all the Activities in the project
    val appComponent by lazy {
        DaggerOnHandApplicationComponent.factory().create(applicationContext)
    }
}