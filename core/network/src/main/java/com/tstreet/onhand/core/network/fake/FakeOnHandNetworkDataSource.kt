package com.tstreet.onhand.core.network.fake

import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.model.NetworkIngredient
import com.tstreet.onhand.core.network.model.NetworkRecipe
import com.tstreet.onhand.core.network.model.NetworkRecipeSearchIngredient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeOnHandNetworkDataSource @Inject constructor() : OnHandNetworkDataSource {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    override fun getIngredients(prefix: String): List<NetworkIngredient> {
        return listOf(
            NetworkIngredient(
                id = 1, image = "potato.jpg", name = "potato"
            ), NetworkIngredient(
                id = 2, image = "carrot.jpg", name = "carrot"
            ), NetworkIngredient(
                id = 3, image = "tomato.jpg", name = "tomato"
            )
        )
    }

    override fun getRecipesFromIngredients(ingredients: List<String>): Flow<List<NetworkRecipe>> {
        println("[OnHand] FakeOnHandNetworkDataSource.getRecipesFromIngredients()")

        return flow {
            emit(
                listOf(
                    NetworkRecipe(
                        id = 10,
                        title = "Grilled Cheese",
                        image = "grilled-cheese.jpg",
                        imageType = "jpg",
                        usedIngredientCount = 2,
                        missedIngredientCount = 1,
                        usedIngredients = listOf(
                            NetworkRecipeSearchIngredient(
                                id = 12,
                                amount = 8.0,
                                unit = "oz",
                                unitLong = "ounces",
                                unitShort = "oz",
                                aisle = "Dairy",
                                name = "Cheddar Cheese",
                                original = "",
                                originalName = "",
                                meta = emptyList(),
                                image = "cheddar.jpg"
                            ), NetworkRecipeSearchIngredient(
                                id = 13,
                                amount = 16.0,
                                unit = "oz",
                                unitLong = "ounces",
                                unitShort = "oz",
                                aisle = "Bread",
                                name = "White Bread",
                                original = "",
                                originalName = "",
                                meta = emptyList(),
                                image = "white-bread.jpg"
                            )
                        ),
                        missedIngredients = listOf(
                            NetworkRecipeSearchIngredient(
                                id = 20,
                                amount = 2.0,
                                unit = "oz",
                                unitLong = "ounces",
                                unitShort = "oz",
                                aisle = "Condiments",
                                name = "Mayo",
                                original = "",
                                originalName = "",
                                meta = emptyList(),
                                image = "mayo.jpg"
                            )
                        ),
                        unusedIngredients = listOf(
                            NetworkRecipeSearchIngredient(
                                id = 999,
                                amount = 200.0,
                                unit = "g",
                                unitLong = "grams",
                                unitShort = "g",
                                aisle = "Vegetables",
                                name = "Green Beans",
                                original = "",
                                originalName = "",
                                meta = emptyList(),
                                image = "green-beans.jpg"
                            )
                        ),
                        likes = 9999
                    ), NetworkRecipe(
                        id = 10,
                        title = "Tomato Soup",
                        image = "tomato-soup.jpg",
                        imageType = "jpg",
                        usedIngredientCount = 2,
                        missedIngredientCount = 1,
                        usedIngredients = listOf(
                            NetworkRecipeSearchIngredient(
                                id = 12,
                                amount = 1.0,
                                unit = "serving",
                                unitLong = "serving",
                                unitShort = "s",
                                aisle = "Vegetables",
                                name = "Roma Tomato",
                                original = "",
                                originalName = "",
                                meta = emptyList(),
                                image = "roma-tomato.jpg"
                            )
                        ),
                        missedIngredients = listOf(
                            NetworkRecipeSearchIngredient(
                                id = 20,
                                amount = 4.0,
                                unit = "cup",
                                unitLong = "cups",
                                unitShort = "c",
                                aisle = "Soup",
                                name = "Vegetable Broth",
                                original = "",
                                originalName = "",
                                meta = emptyList(),
                                image = "vegetable-broth.jpg"
                            )
                        ),
                        unusedIngredients = listOf(),
                        likes = 20
                    )
                )
            )
        }
    }

}
