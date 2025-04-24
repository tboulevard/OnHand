package com.tstreet.onhand

import com.tstreet.onhand.core.common.CommonComponent
import com.tstreet.onhand.core.data.impl.di.DataComponent
import com.tstreet.onhand.core.domain.di.UseCaseModule
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
        DataComponent::class,
        CommonComponent::class
    ],
    modules = [
        UseCaseModule::class
    ]
)
interface OnHandApplicationComponent