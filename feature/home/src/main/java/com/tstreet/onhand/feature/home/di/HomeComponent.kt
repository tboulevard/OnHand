package com.tstreet.onhand.feature.home.di

import com.tstreet.onhand.core.common.CommonComponentProvider
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.data.api.DataComponentProvider
import com.tstreet.onhand.feature.home.HomeViewModel
import dagger.Component

@Component(
    dependencies = [
        DataComponentProvider::class,
        CommonComponentProvider::class
    ],
    modules = [HomeModule::class]
)
@FeatureScope
interface HomeComponent {

    val viewModel: HomeViewModel
}