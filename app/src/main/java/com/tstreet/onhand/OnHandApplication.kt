package com.tstreet.onhand

import android.app.Application

class OnHandApplication : Application() {

    //lateinit var appComponent : OnHandApplicationComponent = DaggerOn

    override fun onCreate() {
        super.onCreate()

        //appComponent = DaggerOnHandApplicationComponent
    }

}