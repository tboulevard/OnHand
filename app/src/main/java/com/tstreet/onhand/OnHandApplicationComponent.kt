package com.tstreet.onhand

import android.content.Context
import com.tstreet.onhand.core.data.di.DataModule
import com.tstreet.onhand.core.network.di.NetworkModule
import com.tstreet.onhand.feature.ingredientsearch.di.IngredientSearchComponent
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
@Component
interface OnHandApplicationComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): OnHandApplicationComponent
    }
}