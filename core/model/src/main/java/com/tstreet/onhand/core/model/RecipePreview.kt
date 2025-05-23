package com.tstreet.onhand.core.model

import com.tstreet.onhand.core.model.data.Ingredient
import kotlinx.serialization.Serializable

/**
 * High-level representation of a recipe, containing identifying information and ingredients
 * to make it. More detailed information to be modeled in [RecipeDetail], which with this class form
 * the complete information for a recipe in [FullRecipe].
 *
 * Note: [MissedIngredients] and [UsedIngredients] are only calculated based on items in user's
 * pantry (i.e. does not include shopping list ingredients).
 */
@Serializable
data class RecipePreview(
    val id: Int,
    val title: String,
    val image: String,
    val imageType: String,
    val usedIngredientCount: Int,
    val usedIngredients: List<Ingredient>,
    val missedIngredientCount: Int,
    val missedIngredients: List<Ingredient>,
    val likes: Int,
    val isCustom: Boolean
)
