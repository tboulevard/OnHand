package com.tstreet.onhand.feature.ingredientsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.tstreet.onhand.OnHandApplication
import com.tstreet.onhand.feature.ingredientsearch.di.DaggerIngredientSearchComponent
import javax.inject.Inject

class IngredientSearchActivity : ComponentActivity() {

    // You want Dagger to provide an instance of LoginViewModel from the Login graph
    @Inject
    lateinit var viewModel: IngredientSearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        val appComponent = (applicationContext as OnHandApplication).appComponent

        // Creates a new instance of LoginComponent
        // Injects the component to populate the @Inject fields
        DaggerIngredientSearchComponent.factory().create(appComponent).inject(this)

        super.onCreate(savedInstanceState)
    }
}