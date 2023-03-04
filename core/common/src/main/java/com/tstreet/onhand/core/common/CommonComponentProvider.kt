package com.tstreet.onhand.core.common

import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import com.tstreet.onhand.core.common.CommonModule.IO
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Named

interface CommonComponentProvider {

    val context : Context
    @get:Named(IO)
    val ioDispatcher: CoroutineDispatcher
}

val LocalCommonProvider = compositionLocalOf<CommonComponentProvider> { error("No common provider found!") }