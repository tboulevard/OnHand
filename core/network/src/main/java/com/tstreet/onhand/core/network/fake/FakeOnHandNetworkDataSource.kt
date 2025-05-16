package com.tstreet.onhand.core.network.fake

import android.util.Log
import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.model.*
import com.tstreet.onhand.core.network.retrofit.NetworkResponse
import com.tstreet.onhand.core.network.retrofit.OnHandNetworkResponse
import kotlinx.coroutines.delay
import javax.inject.Inject

// TODO: move fake models to separate debugImplementation-provided module
class FakeOnHandNetworkDataSource @Inject constructor() : OnHandNetworkDataSource {

    init {
        Log.d("[OnHand]", "Creating ${this.javaClass.simpleName}")
    }

    override suspend fun getIngredients(prefix: String): OnHandNetworkResponse<NetworkIngredientSearchResult> {
        delay(randomArtificialDelay())

        return NetworkResponse.Success(
            body = NetworkIngredientSearchResult(
                results = listOf(
                    NetworkIngredient(
                        id = 9266, image = "pineapple.jpg", name = "pineapple"
                    ),
                    NetworkIngredient(
                        id = 11362, image = "potatoes-yukon-gold.jpg", name = "potato"
                    ),
                    NetworkIngredient(
                        id = 11124, image = "sliced-carrot.jpg", name = "carrot"
                    ),
                    NetworkIngredient(
                        id = 11529, image = "tomato.jpg", name = "tomato"
                    ),
                    NetworkIngredient(
                        id = 11282, image = "brown-onion.jpg", name = "onion"
                    ),
                    NetworkIngredient(
                        id = 11215, image = "garlic.jpg", name = "garlic"
                    ),
                    NetworkIngredient(
                        id = 9003, image = "apple.jpg", name = "apple"
                    )
                ),
                offset = 0,
                number = 10,
                totalResults = 7
            )
        )
    }

    override suspend fun findRecipesFromIngredients(
        ingredients: List<String>
    ): OnHandNetworkResponse<List<NetworkRecipe>> {
        // For testing purposes (progress indicators, etc)
        delay(randomArtificialDelay())

        return NetworkResponse.Success(
            body = listOf(
                NetworkRecipe(
                    id = 634698,
                    title = "Beef Stuffed Manicotti",
                    image = "https://spoonacular.com/recipeImages/634698-312x231.jpg",
                    imageType = "jpg",
                    usedIngredientCount = 3,
                    missedIngredientCount = 4,
                    usedIngredients = listOf(
                        NetworkRecipeIngredient(
                            id = 11215,
                            amount = 3.0,
                            unit = "cloves",
                            unitLong = "cloves",
                            unitShort = "cloves",
                            aisle = "Produce",
                            name = "garlic",
                            original = "3 cloves garlic, minced",
                            originalName = "garlic, minced",
                            meta = listOf("minced"),
                            image = "garlic.png"
                        ),
                        NetworkRecipeIngredient(
                            id = 11282,
                            amount = 1.0,
                            unit = "",
                            unitLong = "",
                            unitShort = "",
                            aisle = "Produce",
                            name = "onion",
                            original = "1 small onion, chopped",
                            originalName = "small onion, chopped",
                            meta = listOf("small", "chopped"),
                            image = "brown-onion.png"
                        ),
                        NetworkRecipeIngredient(
                            id = 11529,
                            amount = 2.0,
                            unit = "cups",
                            unitLong = "cups",
                            unitShort = "cup",
                            aisle = "Produce",
                            name = "tomato",
                            original = "2 cups tomato sauce",
                            originalName = "tomato sauce",
                            meta = listOf("sauce"),
                            image = "tomato-sauce-or-pasta-sauce.jpg"
                        )
                    ),
                    missedIngredients = listOf(
                        NetworkRecipeIngredient(
                            id = 10023572,
                            amount = 1.0,
                            unit = "pound",
                            unitLong = "pound",
                            unitShort = "lb",
                            aisle = "Meat",
                            name = "ground beef",
                            original = "1 pound lean ground beef",
                            originalName = "lean ground beef",
                            meta = listOf("lean"),
                            image = "fresh-ground-beef.jpg"
                        ),
                        NetworkRecipeIngredient(
                            id = 1033,
                            amount = 1.0,
                            unit = "cup",
                            unitLong = "cup",
                            unitShort = "cup",
                            aisle = "Cheese",
                            name = "parmesan cheese",
                            original = "1 cup grated Parmesan cheese",
                            originalName = "grated Parmesan cheese",
                            meta = listOf("grated"),
                            image = "parmesan.jpg"
                        ),
                        NetworkRecipeIngredient(
                            id = 1026,
                            amount = 2.0,
                            unit = "cups",
                            unitLong = "cups",
                            unitShort = "cup",
                            aisle = "Cheese",
                            name = "mozzarella cheese",
                            original = "2 cups shredded mozzarella cheese",
                            originalName = "shredded mozzarella cheese",
                            meta = listOf("shredded"),
                            image = "shredded-cheese-white.jpg"
                        ),
                        NetworkRecipeIngredient(
                            id = 10620420,
                            amount = 8.0,
                            unit = "",
                            unitLong = "",
                            unitShort = "",
                            aisle = "Pasta and Rice",
                            name = "manicotti pasta",
                            original = "8 manicotti shells",
                            originalName = "manicotti shells",
                            meta = listOf(),
                            image = "manicotti.jpg"
                        )
                    ),
                    unusedIngredients = listOf(
                        NetworkRecipeIngredient(
                            id = 11124,
                            amount = 1.0,
                            unit = "medium",
                            unitLong = "medium",
                            unitShort = "medium",
                            aisle = "Produce",
                            name = "carrot",
                            original = "1 medium carrot",
                            originalName = "carrot",
                            meta = listOf(),
                            image = "sliced-carrot.png"
                        )
                    ),
                    likes = 874
                ),
                NetworkRecipe(
                    id = 648279,
                    title = "Italian Garden Frittata",
                    image = "https://spoonacular.com/recipeImages/648279-312x231.jpg",
                    imageType = "jpg",
                    usedIngredientCount = 4,
                    missedIngredientCount = 2,
                    usedIngredients = listOf(
                        NetworkRecipeIngredient(
                            id = 11215,
                            amount = 2.0,
                            unit = "cloves",
                            unitLong = "cloves",
                            unitShort = "cloves",
                            aisle = "Produce",
                            name = "garlic",
                            original = "2 cloves garlic, minced",
                            originalName = "garlic, minced",
                            meta = listOf("minced"),
                            image = "garlic.png"
                        ),
                        NetworkRecipeIngredient(
                            id = 11282,
                            amount = 1.0,
                            unit = "medium",
                            unitLong = "medium",
                            unitShort = "medium",
                            aisle = "Produce",
                            name = "onion",
                            original = "1 medium onion, diced",
                            originalName = "onion, diced",
                            meta = listOf("diced"),
                            image = "brown-onion.png"
                        ),
                        NetworkRecipeIngredient(
                            id = 11529,
                            amount = 2.0,
                            unit = "small",
                            unitLong = "smalls",
                            unitShort = "small",
                            aisle = "Produce",
                            name = "tomato",
                            original = "2 small tomatoes, chopped",
                            originalName = "tomatoes, chopped",
                            meta = listOf("chopped"),
                            image = "tomato.png"
                        ),
                        NetworkRecipeIngredient(
                            id = 11124,
                            amount = 1.0,
                            unit = "medium",
                            unitLong = "medium",
                            unitShort = "medium",
                            aisle = "Produce",
                            name = "carrot",
                            original = "1 medium carrot, shredded",
                            originalName = "carrot, shredded",
                            meta = listOf("shredded"),
                            image = "sliced-carrot.png"
                        )
                    ),
                    missedIngredients = listOf(
                        NetworkRecipeIngredient(
                            id = 1123,
                            amount = 8.0,
                            unit = "large",
                            unitLong = "larges",
                            unitShort = "large",
                            aisle = "Milk, Eggs, Other Dairy",
                            name = "eggs",
                            original = "8 large eggs",
                            originalName = "eggs",
                            meta = listOf(),
                            image = "egg.png"
                        ),
                        NetworkRecipeIngredient(
                            id = 1033,
                            amount = 0.5,
                            unit = "cup",
                            unitLong = "cups",
                            unitShort = "cup",
                            aisle = "Cheese",
                            name = "parmesan cheese",
                            original = "1/2 cup grated Parmesan cheese",
                            originalName = "grated Parmesan cheese",
                            meta = listOf("grated"),
                            image = "parmesan.jpg"
                        )
                    ),
                    unusedIngredients = listOf(
                        NetworkRecipeIngredient(
                            id = 9003,
                            amount = 1.0,
                            unit = "medium",
                            unitLong = "medium",
                            unitShort = "medium",
                            aisle = "Produce",
                            name = "apple",
                            original = "1 medium apple",
                            originalName = "apple",
                            meta = listOf(),
                            image = "apple.jpg"
                        ),
                        NetworkRecipeIngredient(
                            id = 9266,
                            amount = 1.0,
                            unit = "cup",
                            unitLong = "cup",
                            unitShort = "cup",
                            aisle = "Produce",
                            name = "pineapple",
                            original = "1 cup pineapple chunks",
                            originalName = "pineapple chunks",
                            meta = listOf("chunks"),
                            image = "pineapple.jpg"
                        )
                    ),
                    likes = 412
                ),
                NetworkRecipe(
                    id = 716429,
                    title = "Pasta with Garlic, Scallions, Cauliflower & Breadcrumbs",
                    image = "https://spoonacular.com/recipeImages/716429-312x231.jpg",
                    imageType = "jpg",
                    usedIngredientCount = 2,
                    missedIngredientCount = 3,
                    usedIngredients = listOf(
                        NetworkRecipeIngredient(
                            id = 11215,
                            amount = 5.0,
                            unit = "cloves",
                            unitLong = "cloves",
                            unitShort = "cloves",
                            aisle = "Produce",
                            name = "garlic",
                            original = "5 cloves garlic, minced",
                            originalName = "garlic, minced",
                            meta = listOf("minced"),
                            image = "garlic.png"
                        ),
                        NetworkRecipeIngredient(
                            id = 11282,
                            amount = 3.0,
                            unit = "",
                            unitLong = "",
                            unitShort = "",
                            aisle = "Produce",
                            name = "scallions",
                            original = "3 scallions, chopped",
                            originalName = "scallions, chopped",
                            meta = listOf("chopped"),
                            image = "scallions.jpg"
                        )
                    ),
                    missedIngredients = listOf(
                        NetworkRecipeIngredient(
                            id = 10511529,
                            amount = 0.5,
                            unit = "cup",
                            unitLong = "cups",
                            unitShort = "cup",
                            aisle = "Produce",
                            name = "sun-dried tomatoes",
                            original = "1/2 cup sun-dried tomatoes",
                            originalName = "sun-dried tomatoes",
                            meta = listOf("sun-dried"),
                            image = "sundried-tomatoes.jpg"
                        ),
                        NetworkRecipeIngredient(
                            id = 11135,
                            amount = 1.0,
                            unit = "head",
                            unitLong = "head",
                            unitShort = "head",
                            aisle = "Produce",
                            name = "cauliflower",
                            original = "1 head cauliflower, cut into florets",
                            originalName = "cauliflower, cut into florets",
                            meta = listOf("cut into florets"),
                            image = "cauliflower.jpg"
                        ),
                        NetworkRecipeIngredient(
                            id = 20420,
                            amount = 1.0,
                            unit = "pound",
                            unitLong = "pound",
                            unitShort = "lb",
                            aisle = "Pasta and Rice",
                            name = "pasta",
                            original = "1 pound pasta",
                            originalName = "pasta",
                            meta = listOf(),
                            image = "fusilli.jpg"
                        )
                    ),
                    unusedIngredients = listOf(
                        NetworkRecipeIngredient(
                            id = 11124,
                            amount = 1.0,
                            unit = "medium",
                            unitLong = "medium",
                            unitShort = "medium",
                            aisle = "Produce",
                            name = "carrot",
                            original = "1 medium carrot",
                            originalName = "carrot",
                            meta = listOf(),
                            image = "sliced-carrot.png"
                        ),
                        NetworkRecipeIngredient(
                            id = 11529,
                            amount = 1.0,
                            unit = "medium",
                            unitLong = "medium",
                            unitShort = "medium",
                            aisle = "Produce",
                            name = "tomato",
                            original = "1 medium tomato",
                            originalName = "tomato",
                            meta = listOf(),
                            image = "tomato.png"
                        )
                    ),
                    likes = 1238
                ),
                NetworkRecipe(
                    id = 644387,
                    title = "Garlicky Apple Salad With Herbs",
                    image = "https://spoonacular.com/recipeImages/644387-312x231.jpg",
                    imageType = "jpg",
                    usedIngredientCount = 3,
                    missedIngredientCount = 2,
                    usedIngredients = listOf(
                        NetworkRecipeIngredient(
                            id = 9003,
                            amount = 2.0,
                            unit = "",
                            unitLong = "",
                            unitShort = "",
                            aisle = "Produce",
                            name = "apples",
                            original = "2 apples, cored and sliced thin",
                            originalName = "apples, cored and sliced thin",
                            meta = listOf("cored", "sliced", "thin"),
                            image = "apple.jpg"
                        ),
                        NetworkRecipeIngredient(
                            id = 11215,
                            amount = 1.0,
                            unit = "clove",
                            unitLong = "clove",
                            unitShort = "clove",
                            aisle = "Produce",
                            name = "garlic",
                            original = "1 clove garlic, minced",
                            originalName = "garlic, minced",
                            meta = listOf("minced"),
                            image = "garlic.png"
                        ),
                        NetworkRecipeIngredient(
                            id = 11282,
                            amount = 0.25,
                            unit = "cup",
                            unitLong = "cups",
                            unitShort = "cup",
                            aisle = "Produce",
                            name = "red onion",
                            original = "1/4 cup red onion, thinly sliced",
                            originalName = "red onion, thinly sliced",
                            meta = listOf("red", "thinly sliced"),
                            image = "red-onion.png"
                        )
                    ),
                    missedIngredients = listOf(
                        NetworkRecipeIngredient(
                            id = 2044,
                            amount = 0.25,
                            unit = "cup",
                            unitLong = "cups",
                            unitShort = "cup",
                            aisle = "Produce;Spices and Seasonings",
                            name = "fresh basil",
                            original = "1/4 cup fresh basil, chopped",
                            originalName = "fresh basil, chopped",
                            meta = listOf("fresh", "chopped"),
                            image = "basil.jpg"
                        ),
                        NetworkRecipeIngredient(
                            id = 4053,
                            amount = 3.0,
                            unit = "tablespoons",
                            unitLong = "tablespoons",
                            unitShort = "Tbsp",
                            aisle = "Oil, Vinegar, Salad Dressing",
                            name = "olive oil",
                            original = "3 tablespoons olive oil",
                            originalName = "olive oil",
                            meta = listOf(),
                            image = "olive-oil.jpg"
                        )
                    ),
                    unusedIngredients = listOf(
                        NetworkRecipeIngredient(
                            id = 11124,
                            amount = 1.0,
                            unit = "medium",
                            unitLong = "medium",
                            unitShort = "medium",
                            aisle = "Produce",
                            name = "carrot",
                            original = "1 medium carrot",
                            originalName = "carrot",
                            meta = listOf(),
                            image = "sliced-carrot.png"
                        ),
                        NetworkRecipeIngredient(
                            id = 11529,
                            amount = 1.0,
                            unit = "medium",
                            unitLong = "medium",
                            unitShort = "medium",
                            aisle = "Produce",
                            name = "tomato",
                            original = "1 medium tomato",
                            originalName = "tomato",
                            meta = listOf(),
                            image = "tomato.png"
                        )
                    ),
                    likes = 543
                )
            )
        )
    }

    override suspend fun getRecipeDetail(id: Int): OnHandNetworkResponse<NetworkRecipeDetail> {
        delay(randomArtificialDelay())

        // Return different recipe details based on the ID
        return when (id) {
            634698 -> NetworkResponse.Success(
                NetworkRecipeDetail(
                    vegetarian = false,
                    vegan = false,
                    glutenFree = false,
                    dairyFree = false,
                    veryHealthy = false,
                    cheap = false,
                    veryPopular = true,
                    sustainable = false,
                    lowFodmap = false,
                    gaps = "no",
                    preparationMinutes = 25,
                    cookingMinutes = 45,
                    aggregateLikes = 1254,
                    healthScore = 28f,
                    creditsText = "Foodista.com – The Cooking Encyclopedia Everyone Can Edit",
                    sourceName = "Foodista",
                    pricePerServing = 238.45,
                    id = 634698,
                    title = "Beef Stuffed Manicotti",
                    readyInMinutes = 70,
                    servings = 6,
                    sourceUrl = "https://www.foodista.com/recipe/6L5NZDGP/beef-stuffed-manicotti",
                    image = "https://spoonacular.com/recipeImages/634698-556x370.jpg",
                    imageType = "jpg",
                    summary = "Beef Stuffed Manicotti is an Italian main course that serves 6. One serving contains <b>543 calories</b>, <b>27g of protein</b>, and <b>25g of fat</b>. For <b>$2.38 per serving</b>, this recipe covers 26% of your daily requirements of vitamins and minerals. If you have manicotti pasta, onion, ground beef, and a few other ingredients on hand, you can make it. 1254 people have tried and liked this recipe. From preparation to the plate, this recipe takes roughly <b>70 minutes</b>. It is brought to you by Foodista. Overall, this recipe earns a <b>pretty good spoonacular score of 75%</b>. Users who liked this recipe also liked <a href=\"https://spoonacular.com/recipes/beef-stuffed-manicotti-769193\">Beef Stuffed Manicotti</a>, <a href=\"https://spoonacular.com/recipes/beef-stuffed-manicotti-395512\">Beef Stuffed Manicotti</a>, and <a href=\"https://spoonacular.com/recipes/beef-and-cheese-stuffed-manicotti-297415\">Beef and Cheese Stuffed Manicotti</a>.",
                    cuisines = listOf("Italian", "European"),
                    diets = listOf(),
                    occasions = listOf("Sunday dinner", "Family meals"),
                    instructions = "Preheat oven to 350 degree F.\nBring a large pot of lightly salted water to a boil. Add pasta and cook for 8 to 10 minutes or until al dente; drain.\nIn a large skillet over medium heat, brown beef with onion and garlic; drain. Return to heat and add tomato sauce, oregano, 1 teaspoon salt and 1/4 teaspoon pepper; set aside.\nIn a bowl, combine ricotta, Parmesan, eggs, parsley, 1/2 teaspoon salt and 1/8 teaspoon pepper.\nSpread 1/2 cup of the meat sauce in a 9x13 inch baking dish.\nFill each manicotti shell with 3 tablespoons of the cheese mixture and arrange over sauce. Pour remaining sauce over top and sprinkle with mozzarella.\nBake in preheated oven for 35 minutes, until mozzarella is melted and bubbly."
                )
            )

            648279 -> NetworkResponse.Success(
                NetworkRecipeDetail(
                    vegetarian = true,
                    vegan = false,
                    glutenFree = true,
                    dairyFree = false,
                    veryHealthy = true,
                    cheap = true,
                    veryPopular = false,
                    sustainable = true,
                    lowFodmap = false,
                    gaps = "no",
                    preparationMinutes = 10,
                    cookingMinutes = 15,
                    aggregateLikes = 412,
                    healthScore = 86f,
                    creditsText = "Simply Recipes",
                    sourceName = "Simply Recipes",
                    pricePerServing = 154.78,
                    id = 648279,
                    title = "Italian Garden Frittata",
                    readyInMinutes = 25,
                    servings = 4,
                    sourceUrl = "https://www.simplyrecipes.com/recipes/italian_garden_frittata/",
                    image = "https://spoonacular.com/recipeImages/648279-556x370.jpg",
                    imageType = "jpg",
                    summary = "Italian Garden Frittatan is a <b>gluten free and vegetarian</b> main course. One serving contains <b>298 calories</b>, <b>19g of protein</b>, and <b>20g of fat</b>. For <b>$1.55 per serving</b>, this recipe <b>covers 24% of your daily requirements</b> of vitamins and minerals. 412 people have tried and liked this recipe. If you have tomatoes, parmesan cheese, eggs, and a few other ingredients on hand, you can make it. From preparation to the plate, this recipe takes around <b>25 minutes</b>. It is brought to you by Simply Recipes. Overall, this recipe earns a <b>spectacular spoonacular score of 94%</b>. Users who liked this recipe also liked <a href=\"https://spoonacular.com/recipes/italian-garden-frittata-1405765\">Italian Garden Frittata</a>, <a href=\"https://spoonacular.com/recipes/italian-garden-frittata-654812\">Italian Garden Frittata</a>, and <a href=\"https://spoonacular.com/recipes/italian-garden-frittata-1313873\">Italian Garden Frittata</a>.",
                    cuisines = listOf("Mediterranean", "Italian", "European"),
                    diets = listOf("gluten free", "lacto ovo vegetarian"),
                    occasions = listOf("brunch", "breakfast"),
                    instructions = "In a large bowl, whisk together the eggs, salt, and pepper. Stir in the grated Parmesan cheese.\nHeat olive oil in a 10-inch oven-proof skillet over medium-high heat. Add the onions and sauté until translucent, about 2-3 minutes.\nAdd the garlic and cook for another minute.\nAdd the chopped tomatoes and carrots and cook for 2-3 minutes more, until the carrots are slightly softened and the mixture is hot.\nPour the egg mixture over the vegetables in the skillet and stir gently to combine. Lower the heat to medium-low and let cook without stirring until the edges begin to set, about 2-3 minutes.\nPlace the skillet under the broiler and broil until the top is golden and the eggs are completely set, about 3-5 minutes.\nRemove from the oven and let cool for a minute before sliding the frittata onto a serving plate. Cut into wedges and serve warm or at room temperature."
                )
            )

            else -> NetworkResponse.Success(
                NetworkRecipeDetail(
                    vegetarian = false,
                    vegan = false,
                    glutenFree = true,
                    dairyFree = false,
                    veryHealthy = true,
                    cheap = false,
                    veryPopular = true,
                    sustainable = true,
                    lowFodmap = false,
                    gaps = "no",
                    preparationMinutes = 15,
                    cookingMinutes = 25,
                    aggregateLikes = 1238,
                    healthScore = 75f,
                    creditsText = "Bon Appetit",
                    sourceName = "Bon Appetit",
                    pricePerServing = 187.25,
                    id = 716429,
                    title = "Pasta with Garlic, Scallions, Cauliflower & Breadcrumbs",
                    readyInMinutes = 40,
                    servings = 4,
                    sourceUrl = "https://www.bonappetit.com/recipe/pasta-with-garlic-scallions-cauliflower-breadcrumbs",
                    image = "https://spoonacular.com/recipeImages/716429-556x370.jpg",
                    imageType = "jpg",
                    summary = "Pasta with Garlic, Scallions, Cauliflower & Breadcrumbs might be just the main course you are searching for. This recipe makes 2 servings with <b>636 calories</b>, <b>21g of protein</b>, and <b>20g of fat</b> each. For <b>$1.87 per serving</b>, this recipe <b>covers 24% of your daily requirements</b> of vitamins and minerals. From preparation to the plate, this recipe takes about <b>45 minutes</b>. This recipe is liked by 1238 foodies and cooks. If you have pasta, salt and pepper, cheese, and a few other ingredients on hand, you can make it. It is a good option if you're following a <b>lacto ovo vegetarian</b> diet. All things considered, we decided this recipe <b>deserves a spoonacular score of 86%</b>. This score is tremendous. Similar recipes include <a href=\"https://spoonacular.com/recipes/pasta-with-garlic-scallions-cauliflower-breadcrumbs-1204127\">Pasta with Garlic, Scallions, Cauliflower & Breadcrumbs</a>, <a href=\"https://spoonacular.com/recipes/pasta-with-garlic-scallions-cauliflower-breadcrumbs-1545653\">Pasta with Garlic, Scallions, Cauliflower & Breadcrumbs</a>, and <a href=\"https://spoonacular.com/recipes/pasta-with-garlic-scallions-cauliflower-breadcrumbs-1565593\">Pasta with Garlic, Scallions, Cauliflower & Breadcrumbs</a>.",
                    cuisines = listOf("Mediterranean", "Italian", "European"),
                    diets = listOf("gluten free"),
                    occasions = listOf("weeknight dinner", "weekday meal"),
                    instructions = "Cook the pasta in a large pot of boiling salted water until tender but still firm to the bite, stirring occasionally, about 8 minutes. Drain, reserving 1 cup of pasta water.\nMeanwhile, heat the olive oil in a large heavy skillet over medium heat. Add the garlic, cauliflower, and red pepper flakes and sauté until fragrant, about 1 minute. Add the scallions and sun-dried tomatoes and sauté until the scallions are slightly softened, about 2 minutes.\nAdd the cooked pasta, 1/2 cup of the reserved pasta water, and the Parmesan to the vegetable mixture. Toss to coat, adding more reserved pasta water if needed. Season with salt and pepper to taste.\nTransfer the pasta to serving bowls. Sprinkle with the breadcrumbs and serve immediately."
                )
            )
        }
    }
}

private fun randomArtificialDelay() = (500..1200L).random()