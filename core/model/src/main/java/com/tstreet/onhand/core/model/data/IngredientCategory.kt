package com.tstreet.onhand.core.model.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.tstreet.onhand.core.common.R

enum class IngredientCategory(
    @StringRes val displayName: Int,
    @DrawableRes val placeholder: Int
) {
    DAIRY_AND_EGGS(
        R.string.category_dairy_eggs,
        R.drawable.dairy_placeholder
    ),
    MEAT_AND_SEAFOOD(
        R.string.category_meat_seafood,
        R.drawable.meat_and_seafood_placeholder
    ),
    PRODUCE(
        R.string.category_produce,
        R.drawable.produce_placeholder
    ),
    BAKERY_AND_BREAD(
        R.string.category_bakery_bread,
        R.drawable.bakery_and_bread_placeholder
    ),
    CANNED_AND_JARRED(
        R.string.category_canned_jarred,
        R.drawable.canned_and_jarred_placeholder
    ),
    DRY_GOODS_AND_PASTA(
        R.string.category_dry_goods_pasta,
        R.drawable.dry_goods_and_pasta_placeholder
    ),
    BREAKFAST_FOODS(
        R.string.category_breakfast_foods,
        R.drawable.breakfast_foods_placeholder
    ),
    SNACKS_AND_CHIPS(
        R.string.category_snacks_chips,
        R.drawable.snacks_and_chips_placeholder
    ),
    BAKING_INGREDIENTS(
        R.string.category_baking_ingredients,
        R.drawable.baking_ingredients_placeholder
    ),
    CONDIMENTS_AND_SAUCES(
        R.string.category_condiments_sauces,
        R.drawable.condiments_and_sauces_placeholder
    ),
    SPICES_AND_SEASONINGS(
        R.string.category_spices_seasonings,
        R.drawable.spices_and_seasonings_placeholder
    ),
    BEVERAGES(
        R.string.category_beverages,
        R.drawable.beverages_placeholder
    ),
    FROZEN_FOODS(
        R.string.category_frozen_foods,
        R.drawable.frozen_foods_placeholder
    );

    companion object {

        private val allEntries: Array<IngredientCategory> by lazy {
            IngredientCategory.entries.toTypedArray()
        }

        /**
         * For testing purposes.
         */
        fun randomCategory(): IngredientCategory {
            return allEntries.random()
        }
    }
}