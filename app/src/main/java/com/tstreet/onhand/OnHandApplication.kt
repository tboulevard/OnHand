package com.tstreet.onhand

import android.app.Application

class OnHandApplication : Application() {

    val appComponent = DaggerOnHandApplicationComponent.create()

    override fun onCreate() {
        super.onCreate()
    }
}