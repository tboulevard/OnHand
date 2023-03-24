package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.model.RecipeDetail
import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.model.NetworkRecipeDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

class OnlineFirstRecipeDetailRepository @Inject constructor(
    private val onHandNetworkDataSource: Provider<OnHandNetworkDataSource>
) : RecipeDetailRepository {

    override fun getRecipeDetail(id: Int): Flow<RecipeDetail> {
        return onHandNetworkDataSource
            .get()
            .getRecipeDetail(id)
            .map(NetworkRecipeDetail::asExternalModel)
    }
}