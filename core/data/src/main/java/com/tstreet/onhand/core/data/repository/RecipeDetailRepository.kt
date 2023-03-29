package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.model.RecipeDetail
import com.tstreet.onhand.core.network.model.NetworkRecipeDetail
import kotlinx.coroutines.flow.Flow

interface RecipeDetailRepository {

    fun getRecipeDetail(id: Int) : Flow<RecipeDetail>
}

// TODO: move to more appropriate spot
fun NetworkRecipeDetail.asExternalModel() = RecipeDetail(
    id = id,
    // TODO: Determine whether it's best to just transmit an empty src url or some other state
    sourceUrl = sourceUrl ?: EMPTY_SOURCE_URL
)

private const val EMPTY_SOURCE_URL = ""
