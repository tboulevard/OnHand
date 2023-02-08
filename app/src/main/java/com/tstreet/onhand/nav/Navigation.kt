package com.tstreet.onhand.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.tstreet.onhand.feature.ingredientsearch.IngredientSearchScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.IngredientSearch.route) {
        composable(route = Screen.IngredientSearch.route) {
            IngredientSearchScreen()
        }
    }
}

