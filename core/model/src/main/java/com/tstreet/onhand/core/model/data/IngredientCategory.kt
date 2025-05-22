package com.tstreet.onhand.core.model.data

import androidx.annotation.StringRes
import com.tstreet.onhand.core.common.R

enum class IngredientCategory(
    @StringRes val displayName: Int
) {
    DAIRY_AND_EGGS(R.string.category_dairy_eggs),
    MEAT_AND_SEAFOOD(R.string.category_meat_seafood),
    PRODUCE(R.string.category_produce),
    BAKERY_AND_BREAD(R.string.category_bakery_bread),
    CANNED_AND_JARRED(R.string.category_canned_jarred),
    DRY_GOODS_AND_PASTA(R.string.category_dry_goods_pasta),
    BREAKFAST_FOODS(R.string.category_breakfast_foods),
    SNACKS_AND_CHIPS(R.string.category_snacks_chips),
    BAKING_INGREDIENTS(R.string.category_baking_ingredients),
    CONDIMENTS_AND_SAUCES(R.string.category_condiments_sauces),
    SPICES_AND_SEASONINGS(R.string.category_spices_seasonings),
    BEVERAGES(R.string.category_beverages),
    FROZEN_FOODS(R.string.category_frozen_foods);

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