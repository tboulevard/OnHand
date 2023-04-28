package com.tstreet.onhand

import com.tstreet.onhand.core.common.CommonComponentProvider
import com.tstreet.onhand.core.data.api.di.DataComponentProvider
import dagger.Component
import javax.inject.Singleton

/**
 * Definition of the Application graph.
 *
 * Dependencies included here are scoped to application lifecycle, i.e. they stay alive for the
 * lifetime of application (until killed or cleaned up by OS).
 */
@Singleton
@Component(
    dependencies = [
        DataComponentProvider::class,
        CommonComponentProvider::class
    ]
)
interface OnHandApplicationComponent {

    val dataComponent : DataComponentProvider
    val commonComponent : CommonComponentProvider
}