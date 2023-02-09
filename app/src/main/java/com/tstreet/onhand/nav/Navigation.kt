package com.tstreet.onhand.nav

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.tstreet.onhand.OnHandApplication
import com.tstreet.onhand.core.common.daggerViewModel
import com.tstreet.onhand.feature.ingredientsearch.IngredientSearchScreen
import com.tstreet.onhand.feature.ingredientsearch.di.DaggerIngredientSearchComponent

// Ideal set up: Core dependencies such as network, data live for life of application graph
// Feature dependencies only available as needed
@Composable
fun SetupNavigation(applicationContext : Context) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.IngredientSearch.route) {
        composable(route = Screen.IngredientSearch.route) {
            IngredientSearchScreen(
                // TODO: figure out why hilt clone in dagger 2 causes weird compiler error...
                //daggerViewModel {
                    DaggerIngredientSearchComponent.builder().build().getViewModel()
               // }
            )
        }
    }
}

