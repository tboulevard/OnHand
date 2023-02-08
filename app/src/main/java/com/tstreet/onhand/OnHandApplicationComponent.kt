package com.tstreet.onhand

import android.content.Context
//import com.tstreet.onhand.core.network.di.NetworkModule
//import com.tstreet.onhand.core.data.di.DataModule
//import com.tstreet.onhand.core.data.repository.IngredientSearchRepository
import dagger.*
//import javax.inject.Singleton

/**
 * Definition of the Application graph.
 *
 * Put things in here to stay alive for lifetime of application (until killed or cleaned up)
 */
@Component
interface OnHandApplicationComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): OnHandApplicationComponent
    }
}