package com.tstreet.onhand.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.tstreet.onhand.core.common.LocalCommonProvider
import com.tstreet.onhand.core.common.daggerViewModel
import com.tstreet.onhand.core.data.di.LocalDataProvider
import com.tstreet.onhand.feature.ingredientsearch.IngredientSearchScreen
import com.tstreet.onhand.feature.ingredientsearch.di.DaggerIngredientSearchComponent
import com.tstreet.onhand.feature.recipesearch.RecipeSearchScreen
import com.tstreet.onhand.feature.recipesearch.di.DaggerRecipeSearchComponent

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
                        .commonComponentProvider(LocalCommonProvider.current)
                        .build().viewModel
                }
            )
        }
        composable(route = Screen.RecipeSearch.route) {
            println("[OnHand] Navigating to recipe result screen")
            RecipeSearchScreen(
                daggerViewModel {
                    DaggerRecipeSearchComponent.builder()
                        .dataComponentProvider(LocalDataProvider.current)
                        .build().viewModel
                }
            )
        }
    }
}

