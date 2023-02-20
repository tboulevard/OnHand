package com.tstreet.onhand.core.common

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

// TODO: Think about renaming to 'CommonComponent' as responsibility expands for this
@Singleton
@Component
interface ContextComponent : ContextComponentProvider {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ContextComponent
    }
}