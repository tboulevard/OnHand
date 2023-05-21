package com.tstreet.onhand.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
        icon = Icons.Default.Search
    )

    object SavedRecipes : BottomNavigationScreen(
        route = "saved_recipes",
        displayText = "Saved",
        icon = Icons.Default.Favorite
    )

    object ShoppingList : BottomNavigationScreen(
        route = "shopping_list",
        displayText = "Grocery",
        icon = Icons.Default.ShoppingCart
    )

    object AddCustomRecipe : BottomNavigationScreen(
        route = "add_custom_recipe",
        displayText = "Custom",
        icon = Icons.Default.AddCircle
    )
}

val BottomNavigationScreens = listOf(
    BottomNavigationScreen.IngredientSearch,
    BottomNavigationScreen.RecipeSearch,
    BottomNavigationScreen.AddCustomRecipe,
    BottomNavigationScreen.SavedRecipes,
    BottomNavigationScreen.ShoppingList,
)

sealed class Screen(
    val route: String
) {
    object RecipeDetail : Screen("recipe_detail")
}
