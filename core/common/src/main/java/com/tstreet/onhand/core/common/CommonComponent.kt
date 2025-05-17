package com.tstreet.onhand.core.common

import android.content.Context
import android.content.SharedPreferences
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.CommonModule.SHARED_PREF_FILE
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Named
import javax.inject.Singleton

@Component(
    modules = [CommonModule::class]
)
interface CommonComponent {

    val context: Context

    @get:Named(IO)
    val ioDispatcher: CoroutineDispatcher

    @get:Named(SHARED_PREF_FILE)
    val sharedPreferences: SharedPreferences

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): CommonComponent
    }
}