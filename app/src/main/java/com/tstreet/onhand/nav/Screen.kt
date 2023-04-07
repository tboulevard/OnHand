package com.tstreet.onhand.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavigationScreen(
    val route: String,
    val displayText: String,
    val icon: ImageVector
) {
    object IngredientSearch : BottomNavigationScreen(
        route = "ingredient_search",
        displayText = "Home",
        icon = Icons.Default.Home
    )

    object RecipeSearch : BottomNavigationScreen(
        route = "recipe_search",
        displayText = "Recipes",
        icon = Icons.Default.List
    )

    object ShoppingList : BottomNavigationScreen(
        route = "shopping_list",
        displayText = "Shopping List",
        icon = Icons.Default.ShoppingCart
    )
}

val BottomNavigationScreens = listOf(
    BottomNavigationScreen.IngredientSearch,
    BottomNavigationScreen.RecipeSearch,
    BottomNavigationScreen.ShoppingList
)

sealed class Screen(
    val route: String
) {
    object RecipeDetail : Screen("recipe_detail")
}
