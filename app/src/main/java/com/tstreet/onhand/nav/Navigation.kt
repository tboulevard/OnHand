package com.tstreet.onhand.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.tstreet.onhand.core.common.daggerViewModel
import com.tstreet.onhand.core.data.di.LocalDataProvider
import com.tstreet.onhand.feature.ingredientsearch.IngredientSearchScreen
import com.tstreet.onhand.feature.ingredientsearch.di.DaggerIngredientSearchComponent
import com.tstreet.onhand.feature.reciperesult.RecipeResultScreen
import com.tstreet.onhand.feature.reciperesult.di.DaggerRecipeResultComponent

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.IngredientSearch.route) {
        // Note: each composable { } block is triggered for each recomposition (potentially as
        // often each new frame). Revisit whether this is a performance issue later.
        composable(route = Screen.IngredientSearch.route) {
            println("[OnHand] Navigating to search screen")
            IngredientSearchScreen(
                navController,
                daggerViewModel {
                    DaggerIngredientSearchComponent
                        .builder()
                        .dataComponentProvider(LocalDataProvider.current)
                        .build().viewModel
                }
            )
        }
        composable(route = Screen.RecipeResult.route) {
            println("[OnHand] Navigating to recipe result screen")
            RecipeResultScreen(
                daggerViewModel {
                    DaggerRecipeResultComponent.builder()
                        .dataComponentProvider(LocalDataProvider.current)
                        .build().viewModel
                }
            )
        }
    }
}

