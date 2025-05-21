package com.tstreet.onhand.core.domain.usecase.ingredientsearch

import com.tstreet.onhand.core.domain.repository.IngredientSearchRepository
import com.tstreet.onhand.core.domain.repository.PantryRepository
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.domain.IngredientSearchResult
import com.tstreet.onhand.core.model.domain.SuggestedIngredientsResult
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class IngredientSearchUseCaseTest {

    private val ingredientRepository = mock<IngredientSearchRepository>()
    private val pantryRepository = mock<PantryRepository>()
    private lateinit var useCase: IngredientSearchUseCase

    @Before
    fun setup() {
        useCase = IngredientSearchUseCase(ingredientRepository, pantryRepository)
    }

    @Test
    fun `getPantryMapped returns empty list for blank query`() = runBlocking {
        val result = useCase.getPantryMapped("").first()

        assertTrue(result is IngredientSearchResult.Success)
        assertTrue((result as IngredientSearchResult.Success).ingredients.isEmpty())
    }

    @Test
    fun `getPantryMapped returns search results with pantry status`() = runBlocking {
        // Setup test data
        val searchResults = listOf(
            createFakeIngredient(1, "Apple"),
            createFakeIngredient(2, "Banana"),
            createFakeIngredient(3, "Carrot")
        )
        val pantryItems = listOf(
            createFakeIngredient(1, "Apple"),
            createFakeIngredient(3, "Carrot")
        )

        // Mock repository behavior
        `when`(ingredientRepository.searchIngredients("fruit")).thenReturn(flowOf(searchResults))
        `when`(pantryRepository.listPantry(searchResults)).thenReturn(pantryItems)

        // Skip the Loading state and get the first result that is not Loading
        val result =
            useCase.getPantryMapped("fruit").first { it !is IngredientSearchResult.Loading }

        // Verify results
        assertTrue(result is IngredientSearchResult.Success)
        val successResult = result as IngredientSearchResult.Success
        assertEquals(3, successResult.ingredients.size)

        // Verify pantry status
        val appleResult = successResult.ingredients.find { it.ingredient.name == "Apple" }
        val bananaResult = successResult.ingredients.find { it.ingredient.name == "Banana" }
        val carrotResult = successResult.ingredients.find { it.ingredient.name == "Carrot" }

        assertTrue(appleResult?.inPantry == true)
        assertTrue(bananaResult?.inPantry == false)
        assertTrue(carrotResult?.inPantry == true)
    }

    @Test
    fun `getSuggestedIngredients returns only ingredients not in pantry`() = runBlocking {
        // Setup test data
        val popularIngredients = listOf(
            createFakeIngredient(1, "Apple"),
            createFakeIngredient(2, "Banana"),
            createFakeIngredient(3, "Carrot"),
            createFakeIngredient(4, "Potato")
        )
        val pantryItems = listOf(
            createFakeIngredient(1, "Apple"),
            createFakeIngredient(3, "Carrot")
        )

        // Mock repository behavior
        `when`(ingredientRepository.mostPopularIngredients()).thenReturn(flowOf(popularIngredients))
        `when`(pantryRepository.listPantry(popularIngredients)).thenReturn(pantryItems)

        // Execute and skip Loading state
        val result =
            useCase.getSuggestedIngredients().first { it !is SuggestedIngredientsResult.Loading }

        // Verify results
        assertTrue(result is SuggestedIngredientsResult.Success)
        val successResult = result as SuggestedIngredientsResult.Success

        // Should only contain ingredients not in pantry
        assertEquals(2, successResult.ingredients.size)
        val ingredientNames = successResult.ingredients.map { it.ingredient.name }
        assertTrue(ingredientNames.contains("Banana"))
        assertTrue(ingredientNames.contains("Potato"))

        // Should not contain ingredients already in pantry
        assertTrue(!ingredientNames.contains("Apple"))
        assertTrue(!ingredientNames.contains("Carrot"))
    }

    @Test
    fun `getPantryMapped handles error from repository`() = runBlocking {
        // Setup repository to throw exception
        `when`(ingredientRepository.searchIngredients("test")).thenReturn(flow {
            throw RuntimeException(
                "Test exception"
            )
        })

        // Collect and verify the first state is Loading
        val loadingState =
            useCase.getPantryMapped("test").first()
        assertTrue(loadingState is IngredientSearchResult.Loading)

        // Collect again to get the error state (skip Loading state)
        val errorState =
            useCase.getPantryMapped("test").first { it !is IngredientSearchResult.Loading }
        assertTrue(errorState is IngredientSearchResult.Error)
    }

    @Test
    fun `getSuggestedIngredients handles error from repository`() = runBlocking {
        // Setup repository to throw exception
        `when`(ingredientRepository.mostPopularIngredients()).thenReturn(flow {
            throw RuntimeException(
                "Test exception"
            )
        })

        // Collect and verify the first state is Loading
        val loadingState = useCase.getSuggestedIngredients().first()
        assertTrue(loadingState is SuggestedIngredientsResult.Loading)

        // Collect again to get the error state (skip Loading state)
        val errorState =
            useCase.getSuggestedIngredients().first { it !is SuggestedIngredientsResult.Loading }
        assertTrue(errorState is SuggestedIngredientsResult.Error)
    }

    private fun createFakeIngredient(id: Int, name: String): Ingredient {
        return Ingredient(
            id = id,
            name = name
        )
    }
}