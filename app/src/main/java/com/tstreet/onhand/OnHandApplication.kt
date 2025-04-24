package com.tstreet.onhand

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.tstreet.onhand.core.common.DaggerCommonComponent
import com.tstreet.onhand.core.data.impl.di.DaggerDataComponent

class OnHandApplication : Application(), ImageLoaderFactory {

    // Singleton reference to the application graph that is used across the whole app
    lateinit var appComponent: OnHandApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()

        val commonComponent = DaggerCommonComponent.factory().create(this)
        appComponent = DaggerOnHandApplicationComponent
            .builder()
            .commonComponent(commonComponent)
            .build()
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .build()
    }
}

val Application.appComponent: OnHandApplicationComponent
    get() = (this as OnHandApplication).appComponent