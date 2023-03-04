package com.tstreet.onhand.core.common

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

// TODO: Think about renaming to 'CommonComponent' as responsibility expands for this
@Singleton
@Component(
    modules = [CommonModule::class]
)
interface CommonComponent : CommonComponentProvider {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): CommonComponent
    }
}