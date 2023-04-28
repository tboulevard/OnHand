package com.tstreet.onhand.core.data.impl

import com.tstreet.onhand.core.common.CommonComponentProvider
import com.tstreet.onhand.core.data.api.DataComponentProvider
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [CommonComponentProvider::class],
    modules = [DataModule::class]
)
/**
 * TODO: For some reason, attaching [DataComponentProvider] here allows us to to circumvent the
 * 'Singleton component cannot depend on scoped components' error Dagger throws from the
 * [OnHandApplicationComponent]. Look into this further later. For now everything appears to work
 * though...
 */
interface DataComponent : DataComponentProvider