//package com.tstreet.onhand.nav
//
//import android.content.Context
//import androidx.compose.runtime.Composable
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.compose.composable
//import com.tstreet.onhand.OnHandApplication
//import com.tstreet.onhand.core.common.daggerViewModel
//import com.tstreet.onhand.feature.ingredientsearch.IngredientSearchScreen
//import com.tstreet.onhand.feature.ingredientsearch.di.DaggerIngredientSearchComponent
//import com.tstreet.onhand.feature.reciperesult.RecipeResultScreen
//import com.tstreet.onhand.feature.reciperesult.di.DaggerRecipeResultComponent
//
//// Ideal set up: Core dependencies such as network, data live for life of application graph
//// Feature dependencies only available as needed
//@Composable
//fun setupNavigation(
//    applicationContext: Context,
//) {
//    val navController = rememberNavController()
//    NavHost(navController = navController, startDestination = Screen.IngredientSearch.route) {
//        composable(route = Screen.IngredientSearch.route) {
//            // TODO: Why is this spot hit even when we only want to navigate to recipe result screen?
//            // TODO: Core module dependency graph is recreated for each ViewModel. Core modules
//            // TODO: should be under application lifecycle (scope), feature components under feature
//            // TODO: scope
//            println("[OnHand] Navigating to search screen")
//            IngredientSearchScreen(
//                navController,
//                // Allows us to attach viewmodel to lifecycle of parent activity/fragment
//                daggerViewModel {
//                    DaggerIngredientSearchComponent.builder().build().getViewModel()
//                }
//            )
//        }
//        composable(route = Screen.RecipeResult.route) {
//            println("[OnHand] Navigating to recipe result screen")
//            RecipeResultScreen(
//                daggerViewModel {
//                    DaggerRecipeResultComponent.builder().build().getViewModel()
//                }
//            )
//        }
//    }
//}
//
