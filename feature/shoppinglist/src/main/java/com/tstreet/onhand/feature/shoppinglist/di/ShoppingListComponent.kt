package com.tstreet.onhand.feature.shoppinglist.di

import com.tstreet.onhand.core.common.CommonComponentProvider
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.data.di.DataComponentProvider
import com.tstreet.onhand.feature.shoppinglist.ShoppingListViewModel
import dagger.Component

@Component(
    dependencies = [
        DataComponentProvider::class,
        CommonComponentProvider::class
    ],
    modules = [ShoppingListModule::class]
)
@FeatureScope
interface ShoppingListComponent {

    val viewModel : ShoppingListViewModel
}