package com.tstreet.onhand.feature.customrecipe

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class AddCustomRecipeViewModel @Inject constructor() : ViewModel() {

    init {
        println("[OnHand] ${this.javaClass.simpleName} created")
    }
}
