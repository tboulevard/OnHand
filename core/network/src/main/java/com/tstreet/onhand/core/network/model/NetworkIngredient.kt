package com.tstreet.onhand.core.network.model

import kotlinx.serialization.Serializable

@Serializable
class NetworkIngredient(
    val id: Int,
    val name: String,
    val image: String,
    // TODO: Refactor if needed
    //val children: ChildIngredient? = null
)