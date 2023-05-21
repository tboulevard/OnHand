package com.tstreet.onhand.core.domain.customrecipe

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.model.Recipe
import javax.inject.Inject

class AddRecipeUseCase @Inject constructor() : UseCase() {

    operator fun invoke(recipe: Recipe) {
        println("[OnHand] AddRecipeUseCase - Adding recipe=$recipe")
    }
}