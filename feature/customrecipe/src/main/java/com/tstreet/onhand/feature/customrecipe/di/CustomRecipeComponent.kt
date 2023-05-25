package com.tstreet.onhand.feature.customrecipe.di

import androidx.lifecycle.SavedStateHandle
import com.tstreet.onhand.core.common.CommonComponentProvider
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.data.api.di.DataComponentProvider
import com.tstreet.onhand.feature.customrecipe.CreateCustomRecipeViewModel
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [
        DataComponentProvider::class,
        CommonComponentProvider::class
    ],
    modules = [CustomRecipeModule::class]
)
@FeatureScope
interface CustomRecipeComponent {

    val viewModel: CreateCustomRecipeViewModel

    @get:com.tstreet.onhand.feature.customrecipe.di.SavedStateHandle
    val savedStateHandle: SavedStateHandle

    @Component.Factory
    interface Factory {
        fun create(
            dataComponentProvider: DataComponentProvider,
            commonComponentProvider: CommonComponentProvider,
            @BindsInstance @com.tstreet.onhand.feature.customrecipe.di.SavedStateHandle savedStateHandle: SavedStateHandle
        ): CustomRecipeComponent
    }
}