package com.tstreet.onhand

import android.content.Context
import com.tstreet.onhand.core.data.di.DataComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Definition of the Application graph.
 *
 * Dependencies included here are scoped to application lifecycle, i.e. they stay alive for the
 * lifetime of application (until killed or cleaned up by OS).
 */
@Singleton
@Component(
    dependencies = [
        DataComponent::class
    ]
)
interface OnHandApplicationComponent {

//    @Component.Factory
//    interface Factory {
//        fun create(@BindsInstance applicationContext: Context): OnHandApplicationComponent
//    }
}