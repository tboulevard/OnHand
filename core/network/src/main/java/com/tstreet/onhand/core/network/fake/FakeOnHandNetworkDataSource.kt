package com.tstreet.onhand.core.network.fake

import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.model.*
import com.tstreet.onhand.core.network.retrofit.NetworkResponse
import com.tstreet.onhand.core.network.retrofit.OnHandNetworkResponse
import kotlinx.coroutines.delay
import javax.inject.Inject

// TODO: move fake models to separate debugImplementation-provided module
class FakeOnHandNetworkDataSource @Inject constructor() : OnHandNetworkDataSource {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    override suspend fun getIngredients(prefix: String): OnHandNetworkResponse<NetworkIngredientSearchResult> {
        return NetworkResponse.Success(
            body = NetworkIngredientSearchResult(
                results = listOf(
                    NetworkIngredient(
                        id = 1, image = "potato.jpg", name = "potato"
                    ), NetworkIngredient(
                        id = 2, image = "carrot.jpg", name = "carrot"
                    ), NetworkIngredient(
                        id = 3, image = "tomato.jpg", name = "tomato"
                    )
                ),
                offset = 1,
                number = 2,
                totalResults = 3
            )
        )
    }

    override suspend fun findRecipesFromIngredients(
        ingredients: List<String>
    ): OnHandNetworkResponse<List<NetworkRecipe>> {
        // For testing purposes (progress indicators, etc)
        delay(ARTIFICIAL_DELAY_MILLIS)
        return NetworkResponse.Success(
            body = listOf(
                NetworkRecipe(
                    id = 10,
                    title = "Grilled Cheese",
                    image = "grilled-cheese.jpg",
                    imageType = "jpg",
                    usedIngredientCount = 2,
                    missedIngredientCount = 1,
                    usedIngredients = listOf(
                        NetworkRecipeIngredient(
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
                        ), NetworkRecipeIngredient(
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
                        NetworkRecipeIngredient(
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
                        NetworkRecipeIngredient(
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
                    id = 100,
                    title = "Tomato Soup",
                    image = "tomato-soup.jpg",
                    imageType = "jpg",
                    usedIngredientCount = 2,
                    missedIngredientCount = 0,
                    usedIngredients = listOf(
                        NetworkRecipeIngredient(
                            id = 120,
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
                        NetworkRecipeIngredient(
                            id = 200,
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
                ), NetworkRecipe(
                    id = 1000,
                    title = "Cheese Steak",
                    image = "cheese-steak.jpg",
                    imageType = "jpg",
                    usedIngredientCount = 4,
                    missedIngredientCount = 3,
                    usedIngredients = listOf(
                        NetworkRecipeIngredient(
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
                        ), NetworkRecipeIngredient(
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
                        NetworkRecipeIngredient(
                            id = 45,
                            amount = 16.0,
                            unit = "oz",
                            unitLong = "ounces",
                            unitShort = "oz",
                            aisle = "Bread",
                            name = "Ribeye Steak",
                            original = "",
                            originalName = "",
                            meta = emptyList(),
                            image = "ribeye-steak.jpg"
                        )
                    ),
                    unusedIngredients = listOf(),
                    likes = 333
                )
            )
        )
    }

    override suspend fun getRecipeDetail(id: Int): OnHandNetworkResponse<NetworkRecipeDetail> {
        delay(ARTIFICIAL_DELAY_MILLIS)
        return NetworkResponse.Success(
            NetworkRecipeDetail(
                vegetarian = false,
                vegan = false,
                glutenFree = true,
                dairyFree = false,
                veryHealthy = true,
                cheap = false,
                veryPopular = false,
                sustainable = true,
                lowFodmap = false,
                gaps = "yes",
                preparationMinutes = 20,
                cookingMinutes = 30,
                aggregateLikes = 500,
                healthScore = 90,
                creditsText = "Food Network",
                //license = "CC BY-SA 4.0", TODO:
                sourceName = "Food Network",
                pricePerServing = 2.5,
                id = 1234,
                title = "Grilled Chicken Salad",
                readyInMinutes = 50,
                servings = 4,
                sourceUrl = "https://www.foodnetwork.com/recipes/grilled-chicken-salad-" +
                        "recipe-2103233",
                image = "https://spoonacular.com/recipeImages/1234-556x370.jpg",
                imageType = "jpg",
                summary = "This grilled chicken salad is a tasty and healthy option for " +
                        "lunch or dinner. It's packed with protein and vegetables, and the " +
                        "dressing is light and flavorful.",
                cuisines = listOf("American", "Mediterranean"),
                diets = listOf("Low Carb", "High Protein"),
                occasions = listOf("Summer", "Barbecue"),
                instructions = "1. Preheat grill to medium-high heat.\n2. Season chicken " +
                        "breasts with salt and pepper and grill for 6-7 minutes per side, or " +
                        "until cooked through.\n3. Let chicken rest for 5 minutes, then " +
                        "slice into strips.\n4. In a large bowl, combine mixed greens, " +
                        "cherry tomatoes, cucumber, and red onion.\n5. Whisk together olive " +
                        "oil, red wine vinegar, Dijon mustard, honey, salt, and pepper to " +
                        "make the dressing.\n6. Toss salad with dressing and top with sliced " +
                        "chicken."
            )
        )
    }
}

private const val ARTIFICIAL_DELAY_MILLIS = 500L
