package com.tstreet.onhand.feature.recipedetail.di

import com.tstreet.onhand.core.common.CommonComponentProvider
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.data.di.DataComponentProvider
import com.tstreet.onhand.feature.recipedetail.RecipeDetailViewModel
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [
        DataComponentProvider::class,
        CommonComponentProvider::class
    ],
    modules = [RecipeDetailModule::class]
)
@FeatureScope
interface RecipeDetailComponent {

    val viewModel: RecipeDetailViewModel

    @get:RecipeId
    val recipeId : Int

    @Component.Factory
    interface Factory {
        fun create(
            dataComponentProvider: DataComponentProvider,
            commonComponentProvider: CommonComponentProvider,
            @BindsInstance @RecipeId recipeId : Int
        ) : RecipeDetailComponent
    }
}

