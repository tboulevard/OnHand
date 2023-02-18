package com.tstreet.onhand.nav

sealed class Screen(val route : String) {
    object IngredientSearch : Screen("ingredient_search")
    object RecipeResult : Screen("recipe_result")
}
