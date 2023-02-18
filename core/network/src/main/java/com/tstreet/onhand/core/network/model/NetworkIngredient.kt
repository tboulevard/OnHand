package com.tstreet.onhand.core.network.model

import com.tstreet.onhand.core.model.ChildIngredient

class NetworkIngredient(
    val id: Int,
    val name: String,
    val image: String,
    // TODO: Refactor if needed
    val children: ChildIngredient? = null
)