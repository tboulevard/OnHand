package com.tstreet.onhand.core.common

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

@Module
object CommonModule {

    const val IO = "IO"
    const val SHARED_PREF_FILE = "onhand"

    @Provides
    @Singleton
    @Named(IO)
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Named(SHARED_PREF_FILE)
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providePantryStateManager(
        @Named(SHARED_PREF_FILE) sharedPreferences: SharedPreferences
    ): PantryStateManager {
        return PantryStateManagerImpl(sharedPreferences)
    }
}
