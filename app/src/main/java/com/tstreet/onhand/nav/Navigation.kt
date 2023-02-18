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

// TODO: navigation super buggy, look at printed statements when moving around app
@Composable
fun setupNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.IngredientSearch.route) {
        composable(route = Screen.IngredientSearch.route) {
            // TODO: Why is this spot hit even when we only want to navigate to recipe result screen?
            // TODO: Core module dependency graph is recreated for each ViewModel. Core modules
            // TODO: should be under application lifecycle (scope), feature components under feature
            // TODO: scope
            println("[OnHand] Navigating to search screen")
            IngredientSearchScreen(
                navController,
                // Allows us to attach ViewModels to lifecycle of parent activity/fragment
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

