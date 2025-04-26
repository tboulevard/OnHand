package com.tstreet.onhand.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.tstreet.onhand.core.common.CREATE_RECIPE_ROUTE
import com.tstreet.onhand.core.common.PANTRY_INGREDIENT_SEARCH_ROUTE
import com.tstreet.onhand.core.common.RECIPE_DETAIL_ROUTE
import com.tstreet.onhand.core.common.SELECTABLE_INGREDIENT_SEARCH_ROUTE

sealed class BottomNavigationScreen(
    val route: String,
    val displayText: String,
    val icon: ImageVector
) {
    object Home : BottomNavigationScreen(
        route = "home",
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
    BottomNavigationScreen.Home,
    BottomNavigationScreen.RecipeSearch,
    BottomNavigationScreen.AddCustomRecipe,
    BottomNavigationScreen.SavedRecipes,
    BottomNavigationScreen.ShoppingList,
)

sealed class Screen(
    val route: String
) {
    object RecipeDetail : Screen(RECIPE_DETAIL_ROUTE)
    object SelectableIngredientSearch : Screen(PANTRY_INGREDIENT_SEARCH_ROUTE)
    object PantryIngredientSearch : Screen(SELECTABLE_INGREDIENT_SEARCH_ROUTE)
    object CreateRecipe : Screen(CREATE_RECIPE_ROUTE)
}
