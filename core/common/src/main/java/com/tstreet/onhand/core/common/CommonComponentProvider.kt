package com.tstreet.onhand.core.common

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.compositionLocalOf
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.CommonModule.SHARED_PREF_FILE
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Named

interface CommonComponentProvider {

    val context: Context

    @get:Named(IO)
    val ioDispatcher: CoroutineDispatcher

    @get:Named(SHARED_PREF_FILE)
    val sharedPreferences: SharedPreferences
}

val LocalCommonProvider =
    compositionLocalOf<CommonComponentProvider> {
        Log.d("[OnHand]", "LocalCommonProvider not found.")
        error("No common provider found!")
    }
