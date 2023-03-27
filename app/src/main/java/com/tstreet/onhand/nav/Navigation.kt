package com.tstreet.onhand.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tstreet.onhand.core.common.LocalCommonProvider
import com.tstreet.onhand.core.common.injectedViewModel
import com.tstreet.onhand.core.common.injectedViewModelWithArguments
import com.tstreet.onhand.core.data.di.LocalDataProvider
import com.tstreet.onhand.feature.ingredientsearch.IngredientSearchScreen
import com.tstreet.onhand.feature.ingredientsearch.di.DaggerIngredientSearchComponent
import com.tstreet.onhand.feature.recipedetail.RecipeDetailScreen
import com.tstreet.onhand.feature.recipedetail.di.DaggerRecipeDetailComponent
import com.tstreet.onhand.feature.recipesearch.RecipeSearchScreen
import com.tstreet.onhand.feature.recipesearch.di.DaggerRecipeSearchComponent

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val dataProvider = LocalDataProvider.current
    val commonProvider = LocalCommonProvider.current
    NavHost(navController = navController, startDestination = Screen.IngredientSearch.route) {
        // Note: each composable { } block is triggered for each recomposition (potentially as
        // often each new frame). Revisit whether this is a performance issue later.
        composable(route = Screen.IngredientSearch.route) {
            println("[OnHand] Navigating to search screen")
            IngredientSearchScreen(
                navController,
                injectedViewModel {
                    DaggerIngredientSearchComponent
                        .builder()
                        .dataComponentProvider(dataProvider)
                        .commonComponentProvider(commonProvider)
                        .build()
                        .viewModel
                }
            )
        }
        composable(route = Screen.RecipeSearch.route) {
            println("[OnHand] Navigating to recipe result screen")
            // TODO: come back to issue described here ... https://github.com/google/dagger/issues/3188
            // TODO: for some reason this broke when we upgraded compose version
            RecipeSearchScreen(
                navController,
                injectedViewModel {
                    DaggerRecipeSearchComponent.builder()
                        .dataComponentProvider(dataProvider)
                        .commonComponentProvider(commonProvider)
                        .build()
                        .viewModel
                }
            )
        }
        composable(
            route = "${Screen.RecipeDetail.route}/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) {
            println("[OnHand] Navigating to recipe detail screen")
            println("[OnHand] bundle passed to RecipeDetailScreen=${it.arguments.toString()}")
            // TODO: come back to issue described here ... https://github.com/google/dagger/issues/3188
            // TODO: for some reason this broke when we upgraded compose version
            RecipeDetailScreen(
                injectedViewModelWithArguments(bundle = it.arguments) {
                    DaggerRecipeDetailComponent.builder()
                        .dataComponentProvider(dataProvider)
                        .commonComponentProvider(commonProvider)
                        .build()
                        .viewModel
                }
            )
        }
    }
}

