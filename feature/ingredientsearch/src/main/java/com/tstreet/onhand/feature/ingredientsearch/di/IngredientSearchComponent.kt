package com.tstreet.onhand.feature.ingredientsearch.di

//import com.tstreet.onhand.OnHandApplicationComponent
//import com.tstreet.onhand.feature.ingredientsearch.IngredientSearchActivity
//import dagger.Component
//import com.tstreet.onhand.core.data.di.DataModule
//import com.tstreet.onhand.core.network.di.NetworkModule

//@Component(
//    modules = [
//        DataModule::class,
//        NetworkModule::class
//    ],
//    dependencies = [
//        OnHandApplicationComponent::class
//    ]
//)
//interface IngredientSearchComponent {
//
//    @Component.Factory
//    interface Factory {
//        // Takes an instance of OnHandApplicationComponent when creating
//        // an instance of IngredientSearchComponent
//        fun create(appComponent: OnHandApplicationComponent): IngredientSearchComponent
//    }
//
//    fun inject(ingredientSearchFragment: IngredientSearchActivity)
//}