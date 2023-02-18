package com.tstreet.onhand.core.network.fake

import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.model.NetworkIngredient
import javax.inject.Inject

class FakeOnHandNetworkDataSource @Inject constructor() : OnHandNetworkDataSource {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }
    override fun getIngredients(prefix: String): List<NetworkIngredient> {
        return listOf(
            NetworkIngredient(
                id = 1,
                image = "image_here",
                name = "potato"
            ),
            NetworkIngredient(
                id = 2,
                image = "image_here",
                name = "carrot"
            ),
            NetworkIngredient(
                id = 3,
                image = "image_here",
                name = "tomato"
            )
        )
    }

}
