package com.tstreet.onhand.feature.recipedetail.di

import com.tstreet.onhand.core.common.CommonComponent
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.data.di.DataComponent
import com.tstreet.onhand.core.domain.di.UseCaseModule
import com.tstreet.onhand.feature.recipedetail.RecipeDetailViewModel
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [
        DataComponent::class,
        CommonComponent::class
    ],
    modules = [
        UseCaseModule::class
    ]
)
@FeatureScope
interface RecipeDetailComponent {

    val viewModel: RecipeDetailViewModel

    @get:RecipeId
    val recipeId: Int?

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance @RecipeId recipeId: Int?,
            dataComponent: DataComponent,
            commonComponent: CommonComponent
        ): RecipeDetailComponent
    }
}
