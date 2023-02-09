package com.tstreet.onhand

import android.content.Context
import com.tstreet.onhand.core.data.di.DataModule
import com.tstreet.onhand.core.network.di.NetworkModule
import com.tstreet.onhand.feature.ingredientsearch.di.IngredientSearchComponent
import dagger.BindsInstance
import dagger.Component

/**
 * Definition of the Application graph.
 *
 * Put things in here to stay alive for lifetime of application (until killed or cleaned up)
 */
@Component(
    modules = [
        // TODO: do we rely on both being here so that repository can find network module on di graph?
        // i.e.: Should Data module take module dependency on network module instead?
        DataModule::class,
        NetworkModule::class
    ],
//    // TODO: check if this constructs the full graph by just including this here
//    // TODO: also, we rely on core modules inclusion to fill graph in this feature component, is
//    // that the correct way?
//    dependencies = [
//        IngredientSearchComponent::class
//    ]
)
interface OnHandApplicationComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): OnHandApplicationComponent
    }
}