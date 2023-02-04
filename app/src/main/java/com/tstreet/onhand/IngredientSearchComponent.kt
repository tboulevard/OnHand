package com.tstreet.onhand

import com.tstreet.onhand.core.data.di.DataModule
import com.tstreet.onhand.core.network.di.NetworkModule
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Subcomponent(
    modules = [
        NetworkModule::class,
        DataModule::class
    ]
)
interface IngredientSearchComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): IngredientSearchComponent
    }

    fun inject(mainActivity: MainActivity)
}